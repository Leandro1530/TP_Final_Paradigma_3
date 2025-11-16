package VentasDAO.DAO;

import VentasDAO.Conexion.ConexionDB;
import VentasDAO.Interfaz.IFacturaDAO;
import VentasDAO.Objetos.Cliente;
import VentasDAO.Objetos.DetalleFactura;
import VentasDAO.Objetos.Factura;
import VentasDAO.Objetos.FormaPago;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la generación de facturas.
 */
public class FacturaDAO implements IFacturaDAO {

    @Override
    public int crearFactura(Factura factura, List<DetalleFactura> detalles) throws SQLException {
        validarFactura(factura, detalles);
        String sqlFactura = "INSERT INTO factura(numero_factura, id_cliente, id_forma_pago, fecha_generacion, total, observaciones) "
                + "VALUES(?, ?, ?, ?, ?, ?) RETURNING id_factura";
        String sqlDetalle = "INSERT INTO detalle_factura(id_factura, id_producto, cantidad, precio_unitario, subtotal) VALUES(?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement psFactura = cn.prepareStatement(sqlFactura)) {
                psFactura.setString(1, factura.getNumeroFactura());

                Cliente cliente = factura.getCliente();
                if (cliente == null || cliente.getIdCliente() == null || cliente.getIdCliente() <= 0) {
                    throw new SQLException("La factura debe tener un cliente válido");
                }
                psFactura.setInt(2, cliente.getIdCliente());

                FormaPago formaPago = factura.getFormapago();
                if (formaPago == null || formaPago.getIdFormaPago() <= 0) {
                    throw new SQLException("La factura debe tener una forma de pago válida");
                }
                psFactura.setInt(3, formaPago.getIdFormaPago());

                java.util.Date fecha = factura.getFechaGeneracion();
                psFactura.setDate(4, new Date(fecha.getTime()));

                psFactura.setFloat(5, factura.getTotal());

                psFactura.setString(6, factura.getObservaciones());

                int idFactura;
                try (ResultSet rs = psFactura.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("No se pudo obtener el id de la factura generada");
                    }
                    idFactura = rs.getInt(1);
                }

                // ✅ CORRECCIÓN: Validar y usar el producto correctamente
                try (PreparedStatement psDetalle = cn.prepareStatement(sqlDetalle)) {
                    for (DetalleFactura detalle : detalles) {
                        // Validar que el producto existe y tiene ID válido
                        if (detalle.getProducto() == null) {
                            throw new SQLException("Detalle sin producto asociado");
                        }

                        int idProducto = detalle.getProducto().getIdProducto();
                        if (idProducto <= 0) {
                            throw new SQLException("ID de producto inválido: " + idProducto);
                        }

                        psDetalle.setInt(1, idFactura);
                        psDetalle.setInt(2, idProducto); // ✅ Usar el ID del producto correctamente
                        psDetalle.setInt(3, detalle.getCantidad());
                        psDetalle.setFloat(4, detalle.getPrecioUnitario());

                        float subtotal = detalle.getSubtotal();
                        if (subtotal == 0) {
                            subtotal = detalle.getPrecioUnitario() * detalle.getCantidad();
                        }
                        psDetalle.setFloat(5, subtotal);
                        psDetalle.addBatch();
                    }
                    psDetalle.executeBatch();
                }

                cn.commit();
                return idFactura;
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    @Override
    public List<Factura> listar() {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT id_factura, numero_factura, id_cliente, id_forma_pago, fecha_generacion, total, observaciones "
                + "FROM factura ORDER BY id_factura";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Integer idFactura = (Integer) rs.getObject("id_factura");
                String numero = rs.getString("numero_factura");
                java.util.Date fecha = rs.getDate("fecha_generacion");
                float total = rs.getFloat("total");
                String observaciones = rs.getString("observaciones");

                Cliente cliente = null;
                Integer idCliente = (Integer) rs.getObject("id_cliente");
                if (idCliente != null) {
                    cliente = new Cliente();
                    cliente.setIdCliente(idCliente);
                }

                FormaPago formaPago = null;
                Integer idFormaPago = (Integer) rs.getObject("id_forma_pago");
                if (idFormaPago != null) {
                    formaPago = new FormaPago(idFormaPago, null, null);
                }

                facturas.add(new Factura(
                        idFactura != null ? idFactura : 0,
                        numero,
                        fecha,
                        total,
                        observaciones,
                        cliente,
                        formaPago,
                        null));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return facturas;
    }

    private void validarFactura(Factura factura, List<DetalleFactura> detalles) throws SQLException {
        if (factura == null) {
            throw new SQLException("Debe proporcionar la factura");
        }
        if (estaVacio(factura.getNumeroFactura())) {
            throw new SQLException("El número de factura es obligatorio");
        }
        if (factura.getCliente() == null || factura.getCliente().getIdCliente() == null
                || factura.getCliente().getIdCliente() <= 0) {
            throw new SQLException("Debe indicar el cliente de la factura");
        }
        if (factura.getFormapago() == null || factura.getFormapago().getIdFormaPago() <= 0) {
            throw new SQLException("Debe indicar la forma de pago de la factura");
        }
        if (factura.getFechaGeneracion() == null) {
            throw new SQLException("La fecha de generación es obligatoria");
        }
        if (factura.getTotal() <= 0) {
            throw new SQLException("El total de la factura debe ser mayor que cero");
        }
        if (estaVacio(factura.getObservaciones())) {
            throw new SQLException("Las observaciones de la factura son obligatorias");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new SQLException("La factura debe contener al menos un detalle");
        }
        for (DetalleFactura detalle : detalles) {
            validarDetalle(detalle);
        }
    }

    private void validarDetalle(DetalleFactura detalle) throws SQLException {
        if (detalle == null) {
            throw new SQLException("Los detalles no pueden ser nulos");
        }
        if (detalle.getProducto() == null || detalle.getProducto().getIdProducto() <= 0) {
            throw new SQLException("Cada detalle debe tener un producto asociado válido");
        }
        int cantidad = detalle.getCantidad();
        if (cantidad <= 0) {
            throw new SQLException("Cada detalle debe indicar la cantidad");
        }
        if (detalle.getPrecioUnitario() <= 0) {
            throw new SQLException("El precio unitario debe ser mayor que cero");
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
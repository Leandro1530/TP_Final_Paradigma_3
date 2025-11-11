package VentasDAO.DAO;

import VentasDAO.Conexion.ConexionDB;
import VentasDAO.Objetos.DetalleFactura;
import VentasDAO.Objetos.Factura;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * DAO para la generación de facturas.
 */
public class FacturaDAO {

    public int crearFactura(Factura factura, List<DetalleFactura> detalles) throws SQLException {
        String sqlFactura = "INSERT INTO factura(numero_factura, id_cliente, id_forma_pago, fecha_generacion, total, observaciones) "
                + "VALUES(?, ?, ?, ?, ?, ?) RETURNING id_factura";
        String sqlDetalle = "INSERT INTO detalle_factura(id_factura, id_producto, cantidad, precio_unitario, subtotal) VALUES(?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement psFactura = cn.prepareStatement(sqlFactura)) {
                psFactura.setString(1, factura.getNumeroFactura());

                int idCliente = factura.getIdCliente();
                if (idCliente == 0) {
                    psFactura.setNull(2, Types.INTEGER);
                } else {
                    psFactura.setInt(2, idCliente);
                }

                int idFormaPago = factura.getIdFormaPago();
                if (idFormaPago == 0) {
                    psFactura.setNull(3, Types.INTEGER);
                } else {
                    psFactura.setInt(3, idFormaPago);
                }

                // Convertir java.util.Date a java.sql.Date
                java.util.Date fecha = factura.getFechaGeneracion();
                if (fecha != null) {
                    psFactura.setDate(4, new Date(fecha.getTime()));
                } else {
                    psFactura.setDate(4, new Date(System.currentTimeMillis()));
                }

                float total = factura.getTotal();
                psFactura.setFloat(5, total);

                psFactura.setString(6, factura.getObservaciones());

                int idFactura;
                try (ResultSet rs = psFactura.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("No se pudo obtener el id de la factura generada");
                    }
                    idFactura = rs.getInt(1);
                }

                if (detalles != null && !detalles.isEmpty()) {
                    try (PreparedStatement psDetalle = cn.prepareStatement(sqlDetalle)) {
                        for (DetalleFactura detalle : detalles) {
                            psDetalle.setInt(1, idFactura);

                            int idProducto = detalle.getIdProducto();
                            if (idProducto == 0) {
                                throw new SQLException("Cada detalle debe tener un producto asociado");
                            }
                            psDetalle.setInt(2, idProducto);

                            int cantidad = detalle.getCantidad();
                            if (cantidad == 0) {
                                throw new SQLException("Cada detalle debe indicar la cantidad");
                            }
                            psDetalle.setInt(3, cantidad);

                            float precioUnitario = detalle.getPrecioUnitario();
                            psDetalle.setFloat(4, precioUnitario);

                            float subtotal = detalle.getSubtotal();
                            if (subtotal == 0) {
                                subtotal = precioUnitario * cantidad;
                            }
                            psDetalle.setFloat(5, subtotal);

                            psDetalle.addBatch();
                        }
                        psDetalle.executeBatch();
                    }
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
}
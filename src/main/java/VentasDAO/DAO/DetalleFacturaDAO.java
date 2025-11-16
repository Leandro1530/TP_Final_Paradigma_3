package VentasDAO.DAO;

import VentasDAO.Conexion.ConexionDB;
import VentasDAO.Interfaz.IDetalleFacturaDAO;
import VentasDAO.Objetos.DetalleFactura;
import VentasDAO.Objetos.Factura;
import VentasDAO.Objetos.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleFacturaDAO implements IDetalleFacturaDAO {

    private static final String BASE_QUERY =
            "SELECT d.id_detalle, d.id_factura, d.id_producto, d.cantidad, d.precio_unitario, d.subtotal, "
                    + "f.numero_factura, p.nombre "
                    + "FROM detalle_factura d "
                    + "LEFT JOIN factura f ON f.id_factura = d.id_factura "
                    + "LEFT JOIN producto p ON p.id_producto = d.id_producto";

    @Override
    public List<DetalleFactura> listar() {
        return listarConCondicion(null, null);
    }

    @Override
    public List<DetalleFactura> listarPorFactura(Integer idFactura) {
        if (idFactura == null) {
            throw new IllegalArgumentException("El id de la factura es obligatorio");
        }
        return listarConCondicion(" WHERE d.id_factura = ?", ps -> ps.setInt(1, idFactura));
    }

    private List<DetalleFactura> listarConCondicion(String whereClause, SQLConsumer<PreparedStatement> consumer) {
        List<DetalleFactura> detalles = new ArrayList<>();
        String sql = BASE_QUERY + (whereClause != null ? whereClause : "") + " ORDER BY d.id_detalle";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (consumer != null) {
                consumer.accept(ps);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapearDetalle(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return detalles;
    }

    private DetalleFactura mapearDetalle(ResultSet rs) throws SQLException {
        DetalleFactura detalle = new DetalleFactura();
        detalle.setIdDetalle(rs.getInt("id_detalle"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getFloat("precio_unitario"));
        detalle.setSubtotal(rs.getFloat("subtotal"));

        Integer idFactura = (Integer) rs.getObject("id_factura");
        if (idFactura != null) {
            detalle.setFactura(new Factura(
                    idFactura,
                    rs.getString("numero_factura"),
                    null,
                    0f,
                    null,
                    null,
                    null,
                    null));
        }

        Integer idProducto = (Integer) rs.getObject("id_producto");
        if (idProducto != null) {
            Producto producto = new Producto();
            producto.setIdProducto(idProducto);
            producto.setNombre(rs.getString("nombre"));
            detalle.setProducto(producto);
        }
        return detalle;
    }

    @Override
    public void insertar(DetalleFactura detalle) throws SQLException {
        String sql = "INSERT INTO detalle_factura(id_factura, id_producto, cantidad, precio_unitario, subtotal)"
                + " VALUES(?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            prepararDetalle(detalle, ps);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(DetalleFactura detalle) throws SQLException {
        if (detalle.getIdDetalle() <= 0) {
            throw new SQLException("El id del detalle es obligatorio para actualizar");
        }
        String sql = "UPDATE detalle_factura SET id_factura = ?, id_producto = ?, cantidad = ?, precio_unitario = ?, subtotal = ?"
                + " WHERE id_detalle = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            prepararDetalle(detalle, ps);
            ps.setInt(6, detalle.getIdDetalle());
            ps.executeUpdate();
        }
    }

    private void prepararDetalle(DetalleFactura detalle, PreparedStatement ps) throws SQLException {
        Factura factura = detalle.getFactura();
        if (factura == null || factura.getIdFactura() <= 0) {
            throw new SQLException("Debe indicar la factura");
        }
        Producto producto = detalle.getProducto();
        if (producto == null || producto.getIdProducto() <= 0) {
            throw new SQLException("Debe indicar el producto");
        }
        int cantidad = detalle.getCantidad();
        if (cantidad <= 0) {
            throw new SQLException("La cantidad debe ser mayor a cero");
        }
        ps.setInt(1, factura.getIdFactura());
        ps.setInt(2, producto.getIdProducto());
        ps.setInt(3, cantidad);
        ps.setFloat(4, detalle.getPrecioUnitario());
        float subtotal = detalle.getSubtotal();
        if (subtotal == 0) {
            subtotal = detalle.getPrecioUnitario() * cantidad;
        }
        ps.setFloat(5, subtotal);
    }

    @Override
    public void eliminar(Integer idDetalle) throws SQLException {
        if (idDetalle == null || idDetalle <= 0) {
            throw new SQLException("El id del detalle es obligatorio para eliminar");
        }
        String sql = "DELETE FROM detalle_factura WHERE id_detalle = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idDetalle);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminarPorFactura(Integer idFactura) throws SQLException {
        if (idFactura == null || idFactura <= 0) {
            throw new SQLException("El id de la factura es obligatorio para eliminar sus detalles");
        }
        String sql = "DELETE FROM detalle_factura WHERE id_factura = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            ps.executeUpdate();
        }
    }

    @FunctionalInterface
    private interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }
}

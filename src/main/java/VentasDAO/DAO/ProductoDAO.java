package VentasDAO.DAO;

import VentasDAO.Conexion.ConexionDB;
import VentasDAO.Objetos.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones sobre la tabla {@code producto}.
 */
public class ProductoDAO {

    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id_producto, nombre, descripcion, precio, stock, id_categoria FROM producto ORDER BY id_producto";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productos.add(new Producto(
                        (Integer) rs.getObject("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getBigDecimal("precio"),
                        (Integer) rs.getObject("stock"),
                        (Integer) rs.getObject("id_categoria")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return productos;
    }

    public void insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO producto(nombre, descripcion, precio, stock, id_categoria) VALUES(?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());

            BigDecimal precio = producto.getPrecio();
            if (precio == null) {
                ps.setNull(3, Types.NUMERIC);
            } else {
                ps.setBigDecimal(3, precio);
            }

            Integer stock = producto.getStock();
            if (stock == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, stock);
            }

            Integer idCategoria = producto.getIdCategoria();
            if (idCategoria == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, idCategoria);
            }

            ps.executeUpdate();
        }
    }

    public void actualizar(Producto producto) throws SQLException {
        if (producto.getIdProducto() == null) {
            throw new SQLException("El id del producto es obligatorio para actualizar");
        }

        String sql = "UPDATE producto SET nombre = ?, descripcion = ?, precio = ?, stock = ?, id_categoria = ? WHERE id_producto = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());

            BigDecimal precio = producto.getPrecio();
            if (precio == null) {
                ps.setNull(3, Types.NUMERIC);
            } else {
                ps.setBigDecimal(3, precio);
            }

            Integer stock = producto.getStock();
            if (stock == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, stock);
            }

            Integer idCategoria = producto.getIdCategoria();
            if (idCategoria == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, idCategoria);
            }

            ps.setInt(6, producto.getIdProducto());
            ps.executeUpdate();
        }
    }

    public void eliminar(Integer idProducto) throws SQLException {
        if (idProducto == null) {
            throw new SQLException("El id del producto es obligatorio para eliminar");
        }

        String sql = "DELETE FROM producto WHERE id_producto = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ps.executeUpdate();
        }
    }
}

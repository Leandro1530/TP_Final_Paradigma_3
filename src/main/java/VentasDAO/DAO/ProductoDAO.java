package VentasDAO.DAO;

import VentasDAO.Conexion.ConexionDB;
import VentasDAO.Interfaz.IProductoDAO;
import VentasDAO.Objetos.Categoria;
import VentasDAO.Objetos.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ProductoDAO implements IProductoDAO {

    @Override
    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id_producto, nombre, descripcion, precio, stock, id_categoria FROM producto ORDER BY id_producto";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getInt("id_categoria"));
                productos.add(new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("stock"),
                        categoria));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return productos;
    }

    @Override
    public void insertar(Producto producto) throws SQLException {
        validar(producto);
        String sql = "INSERT INTO producto(nombre, descripcion, precio, stock, id_categoria) VALUES(?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());

            ps.setBigDecimal(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setInt(5, obtenerIdCategoria(producto));

            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Producto producto) throws SQLException {
        if (producto.getIdProducto() <= 0) {
            throw new SQLException("El id del producto es obligatorio para actualizar");
        }
        validar(producto);

        String sql = "UPDATE producto SET nombre = ?, descripcion = ?, precio = ?, stock = ?, id_categoria = ? WHERE id_producto = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());

            ps.setBigDecimal(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setInt(5, obtenerIdCategoria(producto));

            ps.setInt(6, producto.getIdProducto());
            ps.executeUpdate();
        }
    }

    @Override
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

    private void validar(Producto producto) throws SQLException {
        if (producto == null) {
            throw new SQLException("Debe proporcionar el producto");
        }
        if (estaVacio(producto.getNombre())) {
            throw new SQLException("El nombre del producto es obligatorio");
        }
        if (estaVacio(producto.getDescripcion())) {
            throw new SQLException("La descripción del producto es obligatoria");
        }
        BigDecimal precio = producto.getPrecio();
        if (precio == null) {
            throw new SQLException("El precio del producto es obligatorio");
        }
        if (producto.getStock() < 0) {
            throw new SQLException("El stock del producto es inválido");
        }
        if (producto.getCategoria() == null || producto.getCategoria().getIdCategoria() <= 0) {
            throw new SQLException("La categoría del producto es obligatoria");
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private int obtenerIdCategoria(Producto producto) throws SQLException {
        if (producto.getCategoria() == null) {
            throw new SQLException("Debe indicar la categoría");
        }
        int idCategoria = producto.getCategoria().getIdCategoria();
        if (idCategoria <= 0) {
            throw new SQLException("La categoría es inválida");
        }
        return idCategoria;
    }
}

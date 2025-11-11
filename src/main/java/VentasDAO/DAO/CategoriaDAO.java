package VentasDAO.DAO;

import VentasDAO.Conexion.ConexionDB;
import VentasDAO.Objetos.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones sobre la tabla {@code categoria}.
 */
public class CategoriaDAO {

    public List<Categoria> listar() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id_categoria, nombre, descripcion FROM categoria ORDER BY id_categoria";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categorias.add(new Categoria(
                        (Integer) rs.getObject("id_categoria"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return categorias;
    }

    public void insertar(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categoria(nombre, descripcion) VALUES(?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.executeUpdate();
        }
    }

    public void actualizar(Categoria categoria) throws SQLException {
        if (categoria.getIdCategoria() == null) {
            throw new SQLException("El id de la categoría es obligatorio para actualizar");
        }

        String sql = "UPDATE categoria SET nombre = ?, descripcion = ? WHERE id_categoria = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setInt(3, categoria.getIdCategoria());
            ps.executeUpdate();
        }
    }

    public void eliminar(Integer idCategoria) throws SQLException {
        if (idCategoria == null) {
            throw new SQLException("El id de la categoría es obligatorio para eliminar");
        }

        String sql = "DELETE FROM categoria WHERE id_categoria = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);
            ps.executeUpdate();
        }
    }
}

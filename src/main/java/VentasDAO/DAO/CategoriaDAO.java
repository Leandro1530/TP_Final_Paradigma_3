package VentasDAO.DAO;

import VentasDAO.Conexion.ConexionDB;
import VentasDAO.Interfaz.ICategoriaDAO;
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
public class CategoriaDAO implements ICategoriaDAO {

    @Override
    public List<Categoria> listar() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id_categoria, nombre, descripcion FROM categoria ORDER BY id_categoria";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categorias.add(new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return categorias;
    }

    @Override
    public void insertar(Categoria categoria) throws SQLException {
        validar(categoria);
        String sql = "INSERT INTO categoria(nombre, descripcion) VALUES(?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Categoria categoria) throws SQLException {
        if (categoria.getIdCategoria() <= 0) {
            throw new SQLException("El id de la categoría es obligatorio para actualizar");
        }
        validar(categoria);

        String sql = "UPDATE categoria SET nombre = ?, descripcion = ? WHERE id_categoria = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setInt(3, categoria.getIdCategoria());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer idCategoria) throws SQLException {
        if (idCategoria == null || idCategoria <= 0) {
            throw new SQLException("El id de la categoría es obligatorio para eliminar");
        }

        String sql = "DELETE FROM categoria WHERE id_categoria = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);
            ps.executeUpdate();
        }
    }

    private void validar(Categoria categoria) throws SQLException {
        if (categoria == null) {
            throw new SQLException("Debe proporcionar la categoría");
        }
        if (estaVacio(categoria.getNombre())) {
            throw new SQLException("El nombre de la categoría es obligatorio");
        }
        if (estaVacio(categoria.getDescripcion())) {
            throw new SQLException("La descripción de la categoría es obligatoria");
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}

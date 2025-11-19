package VentasDAO.DAO;

import VentasDAO.Conexion.ConexionDB;
import VentasDAO.Interfaz.ITipoClienteDAO;
import VentasDAO.Objetos.TipoCliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TipoClienteDAO implements ITipoClienteDAO {

    @Override
    public List<TipoCliente> listar() {
        List<TipoCliente> tipos = new ArrayList<>();
        String sql = "SELECT id_tipo_cliente, nombre, descripcion FROM tipo_cliente ORDER BY id_tipo_cliente";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tipos.add(new TipoCliente(
                        rs.getInt("id_tipo_cliente"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return tipos;
    }

    @Override
    public void insertar(TipoCliente tipoCliente) throws SQLException {
        validar(tipoCliente);
        String sql = "INSERT INTO tipo_cliente(nombre, descripcion) VALUES(?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, tipoCliente.getNombre());
            ps.setString(2, tipoCliente.getDescripcion());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(TipoCliente tipoCliente) throws SQLException {
        if (tipoCliente.getIdTipoCliente() <= 0) {
            throw new SQLException("El id del tipo de cliente es obligatorio para actualizar");
        }
        validar(tipoCliente);

        String sql = "UPDATE tipo_cliente SET nombre = ?, descripcion = ? WHERE id_tipo_cliente = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, tipoCliente.getNombre());
            ps.setString(2, tipoCliente.getDescripcion());
            ps.setInt(3, tipoCliente.getIdTipoCliente());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer idTipoCliente) throws SQLException {
        if (idTipoCliente == null || idTipoCliente <= 0) {
            throw new SQLException("El id del tipo de cliente es obligatorio para eliminar");
        }

        String sql = "DELETE FROM tipo_cliente WHERE id_tipo_cliente = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idTipoCliente);
            ps.executeUpdate();
        }
    }

    private void validar(TipoCliente tipoCliente) throws SQLException {
        if (tipoCliente == null) {
            throw new SQLException("Debe proporcionar el tipo de cliente");
        }
        if (estaVacio(tipoCliente.getNombre())) {
            throw new SQLException("El nombre del tipo de cliente es obligatorio");
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
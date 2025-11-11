package VentasDAO.DAO;

import VentasDAO.Conexion.ConexionDB;
import VentasDAO.Objetos.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> listar() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, apellido, dni, direccion, telefono, email, id_tipo_cliente "
                + "FROM cliente ORDER BY id_cliente";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(new Cliente(
                        (Integer) rs.getObject("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        (Integer) rs.getObject("id_tipo_cliente")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return clientes;
    }

    public void insertar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente(nombre, apellido, dni, direccion, telefono, email, id_tipo_cliente) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDni());
            ps.setString(4, cliente.getDireccion());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getEmail());

            if (cliente.getIdTipoCliente() == null) {
                ps.setNull(7, Types.INTEGER);
            } else {
                ps.setInt(7, cliente.getIdTipoCliente());
            }

            ps.executeUpdate();
        }
    }

    public void actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nombre = ?, apellido = ?, dni = ?, direccion = ?, telefono = ?, email = ?, id_tipo_cliente = ? "
                + "WHERE id_cliente = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDni());
            ps.setString(4, cliente.getDireccion());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getEmail());

            if (cliente.getIdTipoCliente() == null) {
                ps.setNull(7, Types.INTEGER);
            } else {
                ps.setInt(7, cliente.getIdTipoCliente());
            }

            if (cliente.getIdCliente() == null) {
                throw new SQLException("El id del cliente es obligatorio para actualizar");
            }
            ps.setInt(8, cliente.getIdCliente());

            ps.executeUpdate();
        }
    }

    public void eliminar(Integer idCliente) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }
}

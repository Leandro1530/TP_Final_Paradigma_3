package VentasDAO.DAO;

import VentasDAO.Conexion.ConexionDB;
import VentasDAO.Interfaz.IClienteDAO;
import VentasDAO.Objetos.Cliente;
import VentasDAO.Objetos.TipoCliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements IClienteDAO {

    @Override
    public List<Cliente> listar() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, apellido, dni, direccion, telefono, email, id_tipo_cliente "
                + "FROM cliente ORDER BY id_cliente";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TipoCliente tipo = null;
                Integer idTipo = (Integer) rs.getObject("id_tipo_cliente");
                if (idTipo != null) {
                    tipo = new TipoCliente();
                    tipo.setIdTipoCliente(idTipo);
                }
                clientes.add(new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        tipo));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return clientes;
    }

    @Override
    public void insertar(Cliente cliente) throws SQLException {
        validar(cliente);
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

            ps.setInt(7, obtenerIdTipoCliente(cliente));

            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Cliente cliente) throws SQLException {
        validar(cliente);
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

            ps.setInt(7, obtenerIdTipoCliente(cliente));

            if (cliente.getIdCliente() == null || cliente.getIdCliente() <= 0) {
                throw new SQLException("El id del cliente es obligatorio para actualizar");
            }
            ps.setInt(8, cliente.getIdCliente());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer idCliente) throws SQLException {
        if (idCliente == null || idCliente <= 0) {
            throw new SQLException("El id del cliente es obligatorio para eliminar");
        }
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }

    private void validar(Cliente cliente) throws SQLException {
        if (cliente == null) {
            throw new SQLException("Debe proporcionar el cliente");
        }
        if (estaVacio(cliente.getNombre())) {
            throw new SQLException("El nombre del cliente es obligatorio");
        }
        if (estaVacio(cliente.getApellido())) {
            throw new SQLException("El apellido del cliente es obligatorio");
        }
        if (estaVacio(cliente.getDni())) {
            throw new SQLException("El DNI del cliente es obligatorio");
        }
        if (estaVacio(cliente.getDireccion())) {
            throw new SQLException("La dirección del cliente es obligatoria");
        }
        if (estaVacio(cliente.getTelefono())) {
            throw new SQLException("El teléfono del cliente es obligatorio");
        }
        if (estaVacio(cliente.getEmail())) {
            throw new SQLException("El email del cliente es obligatorio");
        }
        if (cliente.getTipoCliente() == null || cliente.getTipoCliente().getIdTipoCliente() <= 0) {
            throw new SQLException("El tipo de cliente es obligatorio");
        }
    }

    private int obtenerIdTipoCliente(Cliente cliente) throws SQLException {
        if (cliente.getTipoCliente() == null) {
            throw new SQLException("Debe indicar el tipo de cliente");
        }
        int idTipo = cliente.getTipoCliente().getIdTipoCliente();
        if (idTipo <= 0) {
            throw new SQLException("El tipo de cliente es inválido");
        }
        return idTipo;
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}

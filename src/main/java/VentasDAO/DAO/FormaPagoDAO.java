package VentasDAO.DAO;

import VentasDAO.Conexion.ConexionDB;
import VentasDAO.Objetos.FormaPago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones sobre la tabla {@code forma_pago}.
 */
public class FormaPagoDAO {

    public List<FormaPago> listar() {
        List<FormaPago> formas = new ArrayList<>();
        String sql = "SELECT id_forma_pago, nombre, descripcion FROM forma_pago ORDER BY id_forma_pago";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                formas.add(new FormaPago(
                        rs.getInt("id_forma_pago"), // int primitivo
                        rs.getString("nombre"),
                        rs.getString("descripcion")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return formas;
    }

    public void insertar(FormaPago formaPago) throws SQLException {
        String sql = "INSERT INTO forma_pago(nombre, descripcion) VALUES(?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, formaPago.getNombre());
            ps.setString(2, formaPago.getDescripcion());
            ps.executeUpdate();
        }
    }

    public void actualizar(FormaPago formaPago) throws SQLException {
        if (formaPago.getIdFormaPago() == 0) { // Validar int primitivo
            throw new SQLException("El id de la forma de pago es obligatorio para actualizar");
        }

        String sql = "UPDATE forma_pago SET nombre = ?, descripcion = ? WHERE id_forma_pago = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, formaPago.getNombre());
            ps.setString(2, formaPago.getDescripcion());
            ps.setInt(3, formaPago.getIdFormaPago()); // int primitivo
            ps.executeUpdate();
        }
    }

    public void eliminar(int idFormaPago) throws SQLException { // Cambiado a int primitivo
        if (idFormaPago == 0) { // Validar int primitivo
            throw new SQLException("El id de la forma de pago es obligatorio para eliminar");
        }

        String sql = "DELETE FROM forma_pago WHERE id_forma_pago = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idFormaPago);
            ps.executeUpdate();
        }
    }
}
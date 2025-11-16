package VentasDAO.Interfaz;

import VentasDAO.Objetos.Cliente;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato de acceso a datos para {@link Cliente}.
 */
public interface IClienteDAO {
    List<Cliente> listar();

    void insertar(Cliente cliente) throws SQLException;

    void actualizar(Cliente cliente) throws SQLException;

    void eliminar(Integer idCliente) throws SQLException;
}

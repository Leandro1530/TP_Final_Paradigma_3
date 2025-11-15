package VentasDAO.Interfaz;

import VentasDAO.Objetos.Cliente;
import java.sql.SQLException;
import java.util.List;

public interface IClienteDAO{
    List<Cliente> listar();
    Cliente obtenerPorId(Integer idCliente);
    void insertar(Cliente cliente) throws SQLException;
    void actualizar(Cliente cliente) throws SQLException;
    void eliminar(Integer idCliente) throws SQLException;
}


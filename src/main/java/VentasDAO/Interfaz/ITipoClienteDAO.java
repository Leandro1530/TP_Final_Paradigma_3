package VentasDAO.Interfaz;

import VentasDAO.Objetos.TipoCliente;

import java.sql.SQLException;
import java.util.List;


public interface ITipoClienteDAO {
    List<TipoCliente> listar();

    void insertar(TipoCliente tipoCliente) throws SQLException;

    void actualizar(TipoCliente tipoCliente) throws SQLException;

    void eliminar(Integer idTipoCliente) throws SQLException;
}

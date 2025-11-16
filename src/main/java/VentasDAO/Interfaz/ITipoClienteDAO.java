package VentasDAO.Interfaz;

import VentasDAO.Objetos.TipoCliente;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato para la persistencia de {@link TipoCliente}.
 */
public interface ITipoClienteDAO {
    List<TipoCliente> listar();

    void insertar(TipoCliente tipoCliente) throws SQLException;

    void actualizar(TipoCliente tipoCliente) throws SQLException;

    void eliminar(Integer idTipoCliente) throws SQLException;
}

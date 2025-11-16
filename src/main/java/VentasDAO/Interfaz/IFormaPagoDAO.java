package VentasDAO.Interfaz;

import VentasDAO.Objetos.FormaPago;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato para la administración de {@link FormaPago}.
 */
public interface IFormaPagoDAO {
    List<FormaPago> listar();

    void insertar(FormaPago formaPago) throws SQLException;

    void actualizar(FormaPago formaPago) throws SQLException;

    void eliminar(Integer idFormaPago) throws SQLException;
}

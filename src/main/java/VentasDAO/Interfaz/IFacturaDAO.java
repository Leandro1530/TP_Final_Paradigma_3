package VentasDAO.Interfaz;

import VentasDAO.Objetos.DetalleFactura;
import VentasDAO.Objetos.Factura;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones esenciales para la generación y consulta de {@link Factura}.
 */
public interface IFacturaDAO {
    int crearFactura(Factura factura, List<DetalleFactura> detalles) throws SQLException;

    List<Factura> listar();
}

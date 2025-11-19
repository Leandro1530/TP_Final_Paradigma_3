package VentasDAO.Interfaz;

import VentasDAO.Objetos.DetalleFactura;
import VentasDAO.Objetos.Factura;

import java.sql.SQLException;
import java.util.List;


public interface IFacturaDAO {
    int crearFactura(Factura factura, List<DetalleFactura> detalles) throws SQLException;

    List<Factura> listar();
}

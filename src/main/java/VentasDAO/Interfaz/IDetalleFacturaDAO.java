package VentasDAO.Interfaz;

import VentasDAO.Objetos.DetalleFactura;

import java.sql.SQLException;
import java.util.List;


public interface IDetalleFacturaDAO {
    List<DetalleFactura> listar();

    List<DetalleFactura> listarPorFactura(Integer idFactura);

    void insertar(DetalleFactura detalle) throws SQLException;

    void actualizar(DetalleFactura detalle) throws SQLException;

    void eliminar(Integer idDetalle) throws SQLException;

    void eliminarPorFactura(Integer idFactura) throws SQLException;
}

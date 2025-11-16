package VentasDAO.Interfaz;

import VentasDAO.Objetos.Producto;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones requeridas para administrar {@link Producto}.
 */
public interface IProductoDAO {
    List<Producto> listar();

    void insertar(Producto producto) throws SQLException;

    void actualizar(Producto producto) throws SQLException;

    void eliminar(Integer idProducto) throws SQLException;
}

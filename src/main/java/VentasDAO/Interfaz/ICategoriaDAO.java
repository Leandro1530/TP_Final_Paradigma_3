package VentasDAO.Interfaz;

import VentasDAO.Objetos.Categoria;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato para las operaciones CRUD de {@link Categoria}.
 */
public interface ICategoriaDAO {
    List<Categoria> listar();

    void insertar(Categoria categoria) throws SQLException;

    void actualizar(Categoria categoria) throws SQLException;

    void eliminar(Integer idCategoria) throws SQLException;
}

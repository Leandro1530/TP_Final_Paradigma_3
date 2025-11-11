package VentasDAO.Objetos;

/**
 * Entidad que representa a la tabla {@code categoria}.
 */
public class Categoria {

    private Integer idCategoria;
    // NOT NULL en la BD: validar antes de insertar/actualizar.
    private String nombre;
    // NOT NULL en la BD: validar antes de insertar/actualizar.
    private String descripcion;

    public Categoria() {
    }

    public Categoria(Integer idCategoria, String nombre, String descripcion) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre != null ? nombre : "Categoría";
    }
}

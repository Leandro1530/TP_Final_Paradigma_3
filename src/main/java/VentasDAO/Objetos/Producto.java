package VentasDAO.Objetos;

import java.math.BigDecimal;

/**
 * Entidad que representa a la tabla {@code producto}.
 */
public class Producto {

    private Integer idProducto;
    // NOT NULL en la BD: validar antes de insertar/actualizar.
    private String nombre;
    // NOT NULL en la BD: validar antes de insertar/actualizar.
    private String descripcion;
    // NOT NULL en la BD: validar antes de insertar/actualizar.
    private BigDecimal precio;
    // NOT NULL en la BD: validar antes de insertar/actualizar.
    private Integer stock;
    // NOT NULL en la BD: validar antes de insertar/actualizar.
    private Integer idCategoria;

    public Producto() {
    }

    public Producto(Integer idProducto,
                    String nombre,
                    String descripcion,
                    BigDecimal precio,
                    Integer stock,
                    Integer idCategoria) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.idCategoria = idCategoria;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    @Override
    public String toString() {
        return nombre != null ? nombre : "Producto";
    }
}

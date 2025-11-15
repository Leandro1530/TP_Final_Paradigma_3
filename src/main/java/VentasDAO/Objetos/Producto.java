package VentasDAO.Objetos;

import java.math.BigDecimal;

/**
 * Entidad que representa a la tabla {@code producto}.
 */
public class Producto {

    private int idProducto;
    // NOT NULL en la BD: validar antes de insertar/actualizar.
    private String nombre;
    // NOT NULL en la BD: validar antes de insertar/actualizar.
    private String descripcion;
    // NOT NULL en la BD: validar antes de insertar/actualizar.
    private BigDecimal precio;
    // NOT NULL en la BD: validar antes de insertar/actualizar.
    private int stock;
    // NOT NULL en la BD: validar antes de insertar/actualizar.
    private Categoria categoria;

    public Producto() {
    }

    public Producto(int idProducto, String nombre, String descripcion, BigDecimal precio, int stock, Categoria categoria) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }


    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }


    @Override
    public String toString() {
        return nombre != null ? nombre : "Producto";
    }
}

package com.yovani.ventas.model;
public class Producto {
    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private Integer idCategoria;
    public Producto() { }
    public Producto(Integer id, String n, String d, Double p, Integer s, Integer idCat) { idProducto=id; nombre=n; descripcion=d; precio=p; stock=s; idCategoria=idCat; }
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer id) { idProducto=id; }
    public String getNombre() { return nombre; }
    public void setNombre(String n) { nombre=n; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String d) { descripcion=d; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double p) { precio=p; }
    public Integer getStock() { return stock; }
    public void setStock(Integer s) { stock=s; }
    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer id) { idCategoria=id; }
    public String toString() { return nombre; }
}

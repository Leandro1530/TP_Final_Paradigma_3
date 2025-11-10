package com.yovani.ventas.model;
public class Categoria {
    private Integer idCategoria;
    private String nombre;
    private String descripcion;
    public Categoria() { }

    public Categoria(Integer id, String n, String d) { idCategoria=id; nombre=n; descripcion=d; }
    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer id) { idCategoria=id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre=nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String d) { descripcion=d; }
    public String toString() { return nombre; }
}

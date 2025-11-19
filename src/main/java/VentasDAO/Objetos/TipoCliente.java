package VentasDAO.Objetos;

public class TipoCliente {
    private int idTipoCliente;
    private String nombre;
    private String descripcion;

    public TipoCliente() {
    }

    public TipoCliente(int idTipoCliente, String nombre, String descripcion) {
        this.idTipoCliente = idTipoCliente;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdTipoCliente() {
        return idTipoCliente;
    }

    public void setIdTipoCliente(int idTipoCliente) {
        this.idTipoCliente = idTipoCliente;
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
        return nombre != null ? nombre : "Tipo de Cliente";
    }
}

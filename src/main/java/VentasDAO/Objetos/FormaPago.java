package VentasDAO.Objetos;

public class FormaPago {

    private int idFormaPago;
    private String nombre;
    private String descripcion;

    public FormaPago(int idFormaPago, String nombre, String descripcion) {
        this.idFormaPago = idFormaPago;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(int idFormaPago) {
        this.idFormaPago = idFormaPago;
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
        return nombre != null ? nombre : "Forma de Pago";
    }
}
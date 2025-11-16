package VentasDAO.Objetos;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Factura {
    private int idFactura;
    private String numeroFactura;
    private Date fechaGeneracion;
    private float total;
    private String observaciones;
    private Cliente cliente;
    private FormaPago formapago;
    private List<DetalleFactura> detallefactura;


    public Factura(int idFactura, String numeroFactura, Date fechaGeneracion, float total, String observaciones, Cliente cliente, FormaPago formapago, List<DetalleFactura> detallefactura) {
        this.idFactura = idFactura;
        this.numeroFactura = numeroFactura;
        this.fechaGeneracion = fechaGeneracion;
        this.total = total;
        this.observaciones = observaciones;
        this.cliente = cliente;
        this.formapago = formapago;
        this.detallefactura = detallefactura;
    }

    public Factura() {

    }


    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Date getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(Date fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public FormaPago getFormapago() {
        return formapago;
    }

    public void setFormapago(FormaPago formapago) {
        this.formapago = formapago;
    }

    public List<DetalleFactura> getDetallefactura() {
        return detallefactura;
    }

    public void setDetallefactura(List<DetalleFactura> detallefactura) {
        this.detallefactura = detallefactura;
    }
}
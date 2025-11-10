package com.yovani.ventas.dao;
import com.yovani.ventas.db.ConexionDB; import com.yovani.ventas.model.*; import java.sql.*; import java.time.LocalDate; import java.util.List;
public class FacturaDAO {
    public int crearFactura(Factura f, List<DetalleFactura> detalles) throws SQLException {
        String sqlF="INSERT INTO factura(numero_factura,id_cliente,id_forma_pago,fecha_generacion,total,observaciones) VALUES(?,?,?,?,?,?) RETURNING id_factura";
        String sqlD="INSERT INTO detalle_factura(id_factura,id_producto,cantidad,precio_unitario,subtotal) VALUES(?,?,?,?,?)";
        try(Connection cn=ConexionDB.getConnection()){ cn.setAutoCommit(false);
            try(PreparedStatement psf=cn.prepareStatement(sqlF)){ psf.setString(1,f.getNumeroFactura()); if(f.getIdCliente()==null) psf.setNull(2,Types.INTEGER); else psf.setInt(2,f.getIdCliente()); if(f.getIdFormaPago()==null) psf.setNull(3,Types.INTEGER); else psf.setInt(3,f.getIdFormaPago()); psf.setDate(4, java.sql.Date.valueOf(f.getFechaGeneracion()!=null? f.getFechaGeneracion(): LocalDate.now())); if(f.getTotal()==null) psf.setNull(5,Types.NUMERIC); else psf.setDouble(5,f.getTotal()); psf.setString(6,f.getObservaciones()); ResultSet rs=psf.executeQuery(); rs.next(); int idFactura=rs.getInt(1);
                try(PreparedStatement psd=cn.prepareStatement(sqlD)){ for(DetalleFactura d: detalles){ psd.setInt(1,idFactura); psd.setInt(2,d.getIdProducto()); psd.setInt(3,d.getCantidad()); psd.setDouble(4,d.getPrecioUnitario()); psd.setDouble(5,d.getSubtotal()); psd.addBatch(); } psd.executeBatch(); }
                cn.commit(); return idFactura;
            } catch(SQLException ex){ cn.rollback(); throw ex; } finally { cn.setAutoCommit(true); }
        }
    }
}

package com.yovani.ventas.dao;
import com.yovani.ventas.db.ConexionDB; import com.yovani.ventas.model.FormaPago; import java.sql.*; import java.util.*;
public class FormaPagoDAO {
    public List<FormaPago> listar() {
        List<FormaPago> out=new ArrayList<>();
        try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("SELECT id_forma_pago,nombre,descripcion FROM forma_pago ORDER BY id_forma_pago"); ResultSet rs=ps.executeQuery()){ while(rs.next()) out.add(new FormaPago(rs.getInt(1),rs.getString(2),rs.getString(3))); } catch(Exception e){e.printStackTrace();}
        return out;
    }
    public void insertar(FormaPago f) throws SQLException { try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("INSERT INTO forma_pago(nombre,descripcion) VALUES(?,?)")){ ps.setString(1,f.getNombre()); ps.setString(2,f.getDescripcion()); ps.executeUpdate(); } }
    public void actualizar(FormaPago f) throws SQLException { try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("UPDATE forma_pago SET nombre=?, descripcion=? WHERE id_forma_pago=?")){ ps.setString(1,f.getNombre()); ps.setString(2,f.getDescripcion()); ps.setInt(3,f.getIdFormaPago()); ps.executeUpdate(); } }
    public void eliminar(int id) throws SQLException { try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("DELETE FROM forma_pago WHERE id_forma_pago=?")){ ps.setInt(1,id); ps.executeUpdate(); } }
}

package com.yovani.ventas.dao;
import com.yovani.ventas.db.ConexionDB; import com.yovani.ventas.model.TipoCliente; import java.sql.*; import java.util.*;
public class TipoClienteDAO {
    public List<TipoCliente> listar() {
        List<TipoCliente> out=new ArrayList<>();
        try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("SELECT id_tipo_cliente,nombre,descripcion FROM tipo_cliente ORDER BY id_tipo_cliente"); ResultSet rs=ps.executeQuery()){ while(rs.next()) out.add(new TipoCliente(rs.getInt(1),rs.getString(2),rs.getString(3))); } catch(Exception e){e.printStackTrace();}
        return out;
    }
    public void insertar(TipoCliente t) throws SQLException { try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("INSERT INTO tipo_cliente(nombre,descripcion) VALUES(?,?)")){ ps.setString(1,t.getNombre()); ps.setString(2,t.getDescripcion()); ps.executeUpdate(); } }
    public void actualizar(TipoCliente t) throws SQLException { try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("UPDATE tipo_cliente SET nombre=?, descripcion=? WHERE id_tipo_cliente=?")){ ps.setString(1,t.getNombre()); ps.setString(2,t.getDescripcion()); ps.setInt(3,t.getIdTipoCliente()); ps.executeUpdate(); } }
    public void eliminar(int id) throws SQLException { try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("DELETE FROM tipo_cliente WHERE id_tipo_cliente=?")){ ps.setInt(1,id); ps.executeUpdate(); } }
}

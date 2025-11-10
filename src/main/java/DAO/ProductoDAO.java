package com.yovani.ventas.dao;
import com.yovani.ventas.db.ConexionDB; import com.yovani.ventas.model.Producto; import java.sql.*; import java.util.*;
public class ProductoDAO {
    public List<Producto> listar() {
        List<Producto> out=new ArrayList<>();
        try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("SELECT id_producto,nombre,descripcion,precio,stock,id_categoria FROM producto ORDER BY id_producto"); ResultSet rs=ps.executeQuery()){ while(rs.next()) out.add(new Producto(rs.getInt(1),rs.getString(2),rs.getString(3), rs.getObject(4)==null?null:((Number)rs.getObject(4)).doubleValue(), (Integer)rs.getObject(5),(Integer)rs.getObject(6))); } catch(Exception e){e.printStackTrace();}
        return out;
    }
    public void insertar(Producto p) throws SQLException {
        try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("INSERT INTO producto(nombre,descripcion,precio,stock,id_categoria) VALUES(?,?,?,?,?)")){ ps.setString(1,p.getNombre()); ps.setString(2,p.getDescripcion()); if(p.getPrecio()==null) ps.setNull(3,Types.NUMERIC); else ps.setDouble(3,p.getPrecio()); if(p.getStock()==null) ps.setNull(4,Types.INTEGER); else ps.setInt(4,p.getStock()); if(p.getIdCategoria()==null) ps.setNull(5,Types.INTEGER); else ps.setInt(5,p.getIdCategoria()); ps.executeUpdate(); }
    }
    public void actualizar(Producto p) throws SQLException {
        try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("UPDATE producto SET nombre=?,descripcion=?,precio=?,stock=?,id_categoria=? WHERE id_producto=?")){ ps.setString(1,p.getNombre()); ps.setString(2,p.getDescripcion()); if(p.getPrecio()==null) ps.setNull(3,Types.NUMERIC); else ps.setDouble(3,p.getPrecio()); if(p.getStock()==null) ps.setNull(4,Types.INTEGER); else ps.setInt(4,p.getStock()); if(p.getIdCategoria()==null) ps.setNull(5,Types.INTEGER); else ps.setInt(5,p.getIdCategoria()); ps.setInt(6,p.getIdProducto()); ps.executeUpdate(); }
    }
    public void eliminar(int id) throws SQLException { try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("DELETE FROM producto WHERE id_producto=?")){ ps.setInt(1,id); ps.executeUpdate(); } }
}

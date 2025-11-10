package com.yovani.ventas.dao;
import com.yovani.ventas.db.ConexionDB; import com.yovani.ventas.model.Categoria; import java.sql.*; import java.util.*;
public class CategoriaDAO {
    public List<Categoria> listar() {
        List<Categoria> out=new ArrayList<>();
        try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("SELECT id_categoria,nombre,descripcion FROM categoria ORDER BY id_categoria"); ResultSet rs=ps.executeQuery()){ while(rs.next()) out.add(new Categoria(rs.getInt(1),rs.getString(2),rs.getString(3))); } catch(Exception e){e.printStackTrace();}
        return out;
    }
    public void insertar(Categoria c) throws SQLException { try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("INSERT INTO categoria(nombre,descripcion) VALUES(?,?)")){ ps.setString(1,c.getNombre()); ps.setString(2,c.getDescripcion()); ps.executeUpdate(); } }
    public void actualizar(Categoria c) throws SQLException { try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("UPDATE categoria SET nombre=?, descripcion=? WHERE id_categoria=?")){ ps.setString(1,c.getNombre()); ps.setString(2,c.getDescripcion()); ps.setInt(3,c.getIdCategoria()); ps.executeUpdate(); } }
    public void eliminar(int id) throws SQLException { try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("DELETE FROM categoria WHERE id_categoria=?")){ ps.setInt(1,id); ps.executeUpdate(); } }
}

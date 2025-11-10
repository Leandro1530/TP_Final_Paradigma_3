package com.yovani.ventas.dao;
import com.yovani.ventas.db.ConexionDB; import com.yovani.ventas.model.Cliente; import java.sql.*; import java.util.*;
public class ClienteDAO {
    public List<Cliente> listar() {
        List<Cliente> out=new ArrayList<>();
        String sql="SELECT id_cliente,nombre,apellido,dni,direccion,telefono,email,id_tipo_cliente FROM cliente ORDER BY id_cliente";
        try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement(sql); ResultSet rs=ps.executeQuery()){ while(rs.next()) out.add(new Cliente(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),(Integer)rs.getObject(8))); } catch(Exception e){e.printStackTrace();}
        return out;
    }
    public void insertar(Cliente c) throws SQLException {
        String sql="INSERT INTO cliente(nombre,apellido,dni,direccion,telefono,email,id_tipo_cliente) VALUES(?,?,?,?,?,?,?)";
        try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement(sql)){ ps.setString(1,c.getNombre()); ps.setString(2,c.getApellido()); ps.setString(3,c.getDni()); ps.setString(4,c.getDireccion()); ps.setString(5,c.getTelefono()); ps.setString(6,c.getEmail()); if(c.getIdTipoCliente()==null) ps.setNull(7,Types.INTEGER); else ps.setInt(7,c.getIdTipoCliente()); ps.executeUpdate(); }
    }
    public void actualizar(Cliente c) throws SQLException {
        String sql="UPDATE cliente SET nombre=?,apellido=?,dni=?,direccion=?,telefono=?,email=?,id_tipo_cliente=? WHERE id_cliente=?";
        try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement(sql)){ ps.setString(1,c.getNombre()); ps.setString(2,c.getApellido()); ps.setString(3,c.getDni()); ps.setString(4,c.getDireccion()); ps.setString(5,c.getTelefono()); ps.setString(6,c.getEmail()); if(c.getIdTipoCliente()==null) ps.setNull(7,Types.INTEGER); else ps.setInt(7,c.getIdTipoCliente()); ps.setInt(8,c.getIdCliente()); ps.executeUpdate(); }
    }
    public void eliminar(int id) throws SQLException { try(Connection cn=ConexionDB.getConnection(); PreparedStatement ps=cn.prepareStatement("DELETE FROM cliente WHERE id_cliente=?")){ ps.setInt(1,id); ps.executeUpdate(); } }
}

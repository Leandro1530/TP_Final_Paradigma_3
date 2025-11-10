package com.yovani.ventas.ui.entities;

import com.yovani.ventas.dao.ProductoDAO;
import com.yovani.ventas.dao.CategoriaDAO;
import com.yovani.ventas.model.Producto;
import com.yovani.ventas.model.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class ProductoFrame extends JDialog {
    private final ProductoDAO dao = new ProductoDAO();
    private final CategoriaDAO catDAO = new CategoriaDAO();
    private JTable table; private JTextField txtNombre, txtDesc, txtPrecio, txtStock; private JComboBox<Categoria> cbCategoria; private Integer selId;

    public ProductoFrame(Window owner) {
        super(owner, "Productos", ModalityType.APPLICATION_MODAL);
        setSize(980,520); setLocationRelativeTo(owner); setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(2,5,8,8));
        txtNombre = new JTextField(); txtDesc = new JTextField();
        txtPrecio = new JTextField(); txtStock = new JTextField(); cbCategoria = new JComboBox<>(catDAO.listar().toArray(new Categoria[0])); cbCategoria.setSelectedIndex(-1);

        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Descripción:")); form.add(txtDesc);
        form.add(new JLabel("Precio:")); form.add(txtPrecio);
        form.add(new JLabel("Stock:")); form.add(txtStock);
        form.add(new JLabel("Categoría:")); form.add(cbCategoria);
        add(form, BorderLayout.NORTH);

        table = new JTable(); add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bSave = new JButton("Guardar/Nuevo");
        JButton bEdit = new JButton("Editar");
        JButton bDel = new JButton("Eliminar");
        buttons.add(bEdit); buttons.add(bDel); buttons.add(bSave);
        add(buttons, BorderLayout.SOUTH);

        bSave.addActionListener(e -> save()); bEdit.addActionListener(e -> edit()); bDel.addActionListener(e -> deleteRow());
        refresh();
    }
    private void refresh() {
        DefaultTableModel m = new DefaultTableModel(new Object[]{"ID","Nombre","Descripción","Precio","Stock","Categoría(ID)"},0);
        for (var p: dao.listar()) m.addRow(new Object[]{p.getIdProducto(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getStock(), p.getIdCategoria()});
        table.setModel(m);
    }
    private void save() {
        Categoria sel = (Categoria) cbCategoria.getSelectedItem();
        Integer idCat = sel==null? null : sel.getIdCategoria();
        Producto p = new Producto(selId, txtNombre.getText(), txtDesc.getText(),
                txtPrecio.getText().isBlank()?null:Double.parseDouble(txtPrecio.getText()),
                txtStock.getText().isBlank()?null:Integer.parseInt(txtStock.getText()),
                idCat);
        try { if (selId==null) dao.insertar(p); else dao.actualizar(p); selId=null; txtNombre.setText(""); txtDesc.setText(""); txtPrecio.setText(""); txtStock.setText(""); cbCategoria.setSelectedIndex(-1); refresh(); }
        catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }
    private void edit() {
        int r = table.getSelectedRow(); if (r<0) return;
        selId = (Integer) table.getValueAt(r,0);
        txtNombre.setText(String.valueOf(table.getValueAt(r,1)));
        txtDesc.setText(String.valueOf(table.getValueAt(r,2)));
        txtPrecio.setText(String.valueOf(table.getValueAt(r,3)));
        txtStock.setText(String.valueOf(table.getValueAt(r,4)));
    }
    private void deleteRow() {
        int r = table.getSelectedRow(); if (r<0) return;
        int id = (Integer) table.getValueAt(r,0);
        if (JOptionPane.showConfirmDialog(this, "Eliminar producto "+id+"?", "Confirmar", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            try { dao.eliminar(id); refresh(); } catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        }
    }
}

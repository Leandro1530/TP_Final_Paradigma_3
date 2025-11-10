package com.yovani.ventas.ui.entities;

import com.yovani.ventas.dao.TipoClienteDAO;
import com.yovani.ventas.model.TipoCliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class TipoClienteFrame extends JDialog {
    private final TipoClienteDAO dao = new TipoClienteDAO();
    private JTable table; private JTextField txtNombre, txtDesc; private Integer selId;

    public TipoClienteFrame(Window owner) {
        super(owner, "Tipos de Cliente", ModalityType.APPLICATION_MODAL);
        setSize(700,420); setLocationRelativeTo(owner); setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(1,4,8,8));
        txtNombre = new JTextField(); txtDesc = new JTextField();
        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Descripción:")); form.add(txtDesc);
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
        DefaultTableModel m = new DefaultTableModel(new Object[]{"ID","Nombre","Descripción"},0);
        for (var t: dao.listar()) m.addRow(new Object[]{t.getIdTipoCliente(), t.getNombre(), t.getDescripcion()});
        table.setModel(m);
    }
    private void save() {
        TipoCliente t = new TipoCliente(selId, txtNombre.getText(), txtDesc.getText());
        try { if (selId==null) dao.insertar(t); else dao.actualizar(t); selId=null; txtNombre.setText(""); txtDesc.setText(""); refresh(); }
        catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }
    private void edit() {
        int r = table.getSelectedRow(); if (r<0) return;
        selId = (Integer) table.getValueAt(r,0);
        txtNombre.setText(String.valueOf(table.getValueAt(r,1)));
        txtDesc.setText(String.valueOf(table.getValueAt(r,2)));
    }
    private void deleteRow() {
        int r = table.getSelectedRow(); if (r<0) return;
        int id = (Integer) table.getValueAt(r,0);
        if (JOptionPane.showConfirmDialog(this, "Eliminar tipo "+id+"?", "Confirmar", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            try { dao.eliminar(id); refresh(); } catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        }
    }
}

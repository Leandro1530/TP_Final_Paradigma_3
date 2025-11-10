package com.yovani.ventas.ui.entities;

import com.yovani.ventas.dao.ClienteDAO;
import com.yovani.ventas.dao.TipoClienteDAO;
import com.yovani.ventas.model.Cliente;
import com.yovani.ventas.model.TipoCliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class ClienteFrame extends JDialog {
    private final ClienteDAO dao = new ClienteDAO();
    private final TipoClienteDAO tipoDAO = new TipoClienteDAO();
    private JTable table;
    private JTextField txtNombre, txtApellido, txtDni, txtDireccion, txtTelefono, txtEmail;
    private JComboBox<TipoCliente> cbTipo;
    private Integer selectedId = null;

    public ClienteFrame(Window owner) {
        super(owner, "Clientes", ModalityType.APPLICATION_MODAL);
        setSize(980,560); setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(3,4,8,8));
        txtNombre = new JTextField(); txtApellido = new JTextField();
        txtDni = new JTextField(); txtDireccion = new JTextField();
        txtTelefono = new JTextField(); txtEmail = new JTextField();
        cbTipo = new JComboBox<>(tipoDAO.listar().toArray(new TipoCliente[0]));
        cbTipo.setSelectedIndex(-1);

        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Apellido:")); form.add(txtApellido);
        form.add(new JLabel("DNI:")); form.add(txtDni);
        form.add(new JLabel("Dirección:")); form.add(txtDireccion);
        form.add(new JLabel("Teléfono:")); form.add(txtTelefono);
        form.add(new JLabel("Email:")); form.add(txtEmail);
        form.add(new JLabel("Tipo Cliente:")); form.add(cbTipo);
        add(form, BorderLayout.NORTH);

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNuevo = new JButton("Guardar/Nuevo");
        JButton btnEditar = new JButton("Editar selección");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        buttons.add(btnLimpiar); buttons.add(btnEditar); buttons.add(btnEliminar); buttons.add(btnNuevo);
        add(buttons, BorderLayout.SOUTH);

        btnNuevo.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> cargarSeleccion());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        refrescar();
    }
    private void refrescar() {
        var data = dao.listar();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Nombre","Apellido","DNI","Dirección","Teléfono","Email","Tipo"}, 0);
        for (var c: data) model.addRow(new Object[]{c.getIdCliente(), c.getNombre(), c.getApellido(), c.getDni(), c.getDireccion(), c.getTelefono(), c.getEmail(), c.getIdTipoCliente()});
        table.setModel(model);
    }
    private void limpiar() {
        selectedId = null; txtNombre.setText(""); txtApellido.setText(""); txtDni.setText(""); txtDireccion.setText(""); txtTelefono.setText(""); txtEmail.setText(""); cbTipo.setSelectedIndex(-1);
    }
    private void guardar() {
        TipoCliente sel = (TipoCliente) cbTipo.getSelectedItem();
        Integer idTipo = sel==null? null : sel.getIdTipoCliente();
        Cliente c = new Cliente(selectedId, txtNombre.getText(), txtApellido.getText(), txtDni.getText(),
                txtDireccion.getText(), txtTelefono.getText(), txtEmail.getText(), idTipo);
        try {
            if (selectedId==null) dao.insertar(c); else dao.actualizar(c);
            limpiar(); refrescar();
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    }
    private void cargarSeleccion() {
        int row = table.getSelectedRow(); if (row<0) return;
        selectedId = (Integer) table.getValueAt(row,0);
        txtNombre.setText(String.valueOf(table.getValueAt(row,1)));
        txtApellido.setText(String.valueOf(table.getValueAt(row,2)));
        txtDni.setText(String.valueOf(table.getValueAt(row,3)));
        txtDireccion.setText(String.valueOf(table.getValueAt(row,4)));
        txtTelefono.setText(String.valueOf(table.getValueAt(row,5)));
        txtEmail.setText(String.valueOf(table.getValueAt(row,6)));
    }
    private void eliminar() {
        int row = table.getSelectedRow(); if (row<0) return;
        int id = (Integer) table.getValueAt(row,0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar cliente "+id+"?", "Confirmar", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            try { dao.eliminar(id); refrescar(); } catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        }
    }
}

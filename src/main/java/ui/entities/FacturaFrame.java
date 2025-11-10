package com.yovani.ventas.ui.entities;

import com.yovani.ventas.dao.*;
import com.yovani.ventas.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FacturaFrame extends JDialog {
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final FormaPagoDAO formaPagoDAO = new FormaPagoDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final FacturaDAO facturaDAO = new FacturaDAO();

    private JComboBox<Cliente> cbCliente;
    private JComboBox<FormaPago> cbForma;
    private JTable tablaDetalles;
    private JTextField txtNumero, txtObs;
    private JLabel lblTotal;

    public FacturaFrame(Window owner) {
        super(owner, "Nueva Factura", ModalityType.APPLICATION_MODAL);
        setSize(1000,600); setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(2,4,8,8));
        txtNumero = new JTextField("F-"+System.currentTimeMillis()); txtObs = new JTextField();
        cbCliente = new JComboBox<>();
        for (var c : clienteDAO.listar()) cbCliente.addItem(c);
        cbForma = new JComboBox<>();
        for (var f : formaPagoDAO.listar()) cbForma.addItem(f);
        top.add(new JLabel("N° Factura:")); top.add(txtNumero);
        top.add(new JLabel("Cliente:")); top.add(cbCliente);
        top.add(new JLabel("Forma de Pago:")); top.add(cbForma);
        top.add(new JLabel("Observaciones:")); top.add(txtObs);
        add(top, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID Producto","Nombre","Precio","Cantidad","Subtotal"},0);
        tablaDetalles = new JTable(model);
        add(new JScrollPane(tablaDetalles), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Agregar Producto");
        JButton btnQuitar = new JButton("Quitar");
        JButton btnGuardar = new JButton("Guardar Factura");
        lblTotal = new JLabel("Total: 0.0");
        bottom.add(btnAgregar); bottom.add(btnQuitar); bottom.add(lblTotal); bottom.add(btnGuardar);
        add(bottom, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnQuitar.addActionListener(e -> quitar());
        btnGuardar.addActionListener(e -> guardar());
    }

    private void agregarProducto() {
        var productos = productoDAO.listar();
        if (productos.isEmpty()) { JOptionPane.showMessageDialog(this, "No hay productos"); return; }
        Producto p = (Producto) JOptionPane.showInputDialog(this, "Seleccione producto","Productos",
                JOptionPane.PLAIN_MESSAGE, null, productos.toArray(), productos.get(0));
        if (p==null) return;
        String cantStr = JOptionPane.showInputDialog(this, "Cantidad:", "1");
        if (cantStr==null || cantStr.isBlank()) return;
        int cant = Integer.parseInt(cantStr);
        double precio = p.getPrecio()==null?0.0:p.getPrecio();
        double subtotal = precio*cant;
        ((DefaultTableModel)tablaDetalles.getModel()).addRow(new Object[]{p.getIdProducto(), p.getNombre(), precio, cant, subtotal});
        recalcular();
    }
    private void quitar() {
        int r = tablaDetalles.getSelectedRow(); if (r>=0) ((DefaultTableModel)tablaDetalles.getModel()).removeRow(r);
        recalcular();
    }
    private void recalcular() {
        double total=0.0; 
        for (int i=0;i<tablaDetalles.getRowCount();i++) total += Double.parseDouble(tablaDetalles.getValueAt(i,4).toString());
        lblTotal.setText("Total: "+total);
    }
    private void guardar() {
        try {
            List<DetalleFactura> dets = new ArrayList<>();
            for (int i=0;i<tablaDetalles.getRowCount();i++) {
                int idProd = Integer.parseInt(tablaDetalles.getValueAt(i,0).toString());
                int cant = Integer.parseInt(tablaDetalles.getValueAt(i,3).toString());
                double precio = Double.parseDouble(tablaDetalles.getValueAt(i,2).toString());
                dets.add(new DetalleFactura(null, null, idProd, cant, precio, precio*cant));
            }
            Cliente c = (Cliente) cbCliente.getSelectedItem();
            FormaPago f = (FormaPago) cbForma.getSelectedItem();
            double total = Double.parseDouble(lblTotal.getText().replace("Total: ",""));
            Factura fac = new Factura(null, txtNumero.getText(), c==null? null : c.getIdCliente(), f==null? null : f.getIdFormaPago(), LocalDate.now(), total, txtObs.getText());
            int id = facturaDAO.crearFactura(fac, dets);
            JOptionPane.showMessageDialog(this, "Factura guardada con ID: "+id);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

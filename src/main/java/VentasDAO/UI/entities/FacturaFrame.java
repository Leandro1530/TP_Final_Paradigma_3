package VentasDAO.UI.entities;

import VentasDAO.DAO.*;
import VentasDAO.Objetos.*;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Ventana sencilla para generar facturas y sus detalles.
 */
public class FacturaFrame extends JDialog {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final FormaPagoDAO formaPagoDAO = new FormaPagoDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final FacturaDAO facturaDAO = new FacturaDAO();

    private final JComboBox<Cliente> cbCliente = new JComboBox<>();
    private final JComboBox<FormaPago> cbFormaPago = new JComboBox<>();
    private final JTable tablaDetalles = new JTable();
    private final JTextField txtNumero = new JTextField();
    private final JTextField txtObservaciones = new JTextField();
    private final JLabel lblTotal = new JLabel("Total: 0");

    public FacturaFrame(java.awt.Window owner) {
        super(owner, "Factura", ModalityType.APPLICATION_MODAL);
        setSize(960, 560);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel panelCabecera = new JPanel(new GridLayout(2, 4, 8, 8));
        txtNumero.setText("F-" + System.currentTimeMillis());

        panelCabecera.add(new JLabel("Número:"));
        panelCabecera.add(txtNumero);
        panelCabecera.add(new JLabel("Cliente:"));
        panelCabecera.add(cbCliente);
        panelCabecera.add(new JLabel("Forma de pago:"));
        panelCabecera.add(cbFormaPago);
        panelCabecera.add(new JLabel("Observaciones:"));
        panelCabecera.add(txtObservaciones);
        add(panelCabecera, BorderLayout.NORTH);

        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID Producto", "Nombre", "Precio", "Cantidad", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDetalles.setModel(modelo);
        add(new JScrollPane(tablaDetalles), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnQuitar = new JButton("Quitar");
        JButton btnGuardar = new JButton("Guardar");
        panelBotones.add(btnAgregar);
        panelBotones.add(btnQuitar);
        panelBotones.add(lblTotal);
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnQuitar.addActionListener(e -> quitarProducto());
        btnGuardar.addActionListener(e -> guardarFactura());

        cargarCombos();
    }

    private void cargarCombos() {
        cbCliente.removeAllItems();
        for (Cliente cliente : clienteDAO.listar()) {
            cbCliente.addItem(cliente);
        }
        cbFormaPago.removeAllItems();
        for (FormaPago formaPago : formaPagoDAO.listar()) {
            cbFormaPago.addItem(formaPago);
        }
    }

    private void agregarProducto() {
        List<Producto> productos = productoDAO.listar();
        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos disponibles");
            return;
        }
        Producto producto = (Producto) JOptionPane.showInputDialog(this,
                "Seleccione un producto",
                "Productos",
                JOptionPane.PLAIN_MESSAGE,
                null,
                productos.toArray(),
                productos.get(0));
        if (producto == null) {
            return;
        }
        String cantidadTexto = JOptionPane.showInputDialog(this, "Cantidad", "1");
        if (cantidadTexto == null || cantidadTexto.trim().isEmpty()) {
            return;
        }
        try {
            int cantidad = Integer.parseInt(cantidadTexto);
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }
            BigDecimal precio = producto.getPrecio() != null ? producto.getPrecio() : BigDecimal.ZERO;
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));

            ((DefaultTableModel) tablaDetalles.getModel()).addRow(new Object[]{
                    producto.getIdProducto(),
                    producto.getNombre(),
                    precio,
                    cantidad,
                    subtotal
            });
            recalcularTotal();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarProducto() {
        int fila = tablaDetalles.getSelectedRow();
        if (fila >= 0) {
            ((DefaultTableModel) tablaDetalles.getModel()).removeRow(fila);
            recalcularTotal();
        }
    }

    private void recalcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < tablaDetalles.getRowCount(); i++) {
            Object valor = tablaDetalles.getValueAt(i, 4);
            if (valor instanceof BigDecimal) {
                total = total.add((BigDecimal) valor);
            } else if (valor != null) {
                total = total.add(new BigDecimal(valor.toString()));
            }
        }
        lblTotal.setText("Total: " + total);
    }

    private void guardarFactura() {
        try {
            // Validar selecciones
            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            FormaPago formaPago = (FormaPago) cbFormaPago.getSelectedItem();

            if (cliente == null || formaPago == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar cliente y forma de pago", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar que hay detalles
            if (tablaDetalles.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Debe agregar al menos un detalle", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Calcular total correctamente como float
            BigDecimal totalBD = BigDecimal.ZERO;
            for (int i = 0; i < tablaDetalles.getRowCount(); i++) {
                Object valor = tablaDetalles.getValueAt(i, 4);
                if (valor instanceof BigDecimal) {
                    totalBD = totalBD.add((BigDecimal) valor);
                } else if (valor != null) {
                    totalBD = totalBD.add(new BigDecimal(valor.toString()));
                }
            }
            float total = totalBD.floatValue();

            // Crear factura con Date y float (tipos correctos según Factura.java)
            Factura factura = new Factura(
                    0, // idFactura se generará en la BD
                    txtNumero.getText(),
                    cliente.getIdCliente(),
                    formaPago.getIdFormaPago(),
                    new Date(), // java.util.Date
                    total, // float
                    txtObservaciones.getText(),
                    cliente,
                    formaPago,
                    null // los detalles se pasan por separado
            );

            List<DetalleFactura> detalles = new ArrayList<>();
            for (int i = 0; i < tablaDetalles.getRowCount(); i++) {
                Integer idProducto = (Integer) tablaDetalles.getValueAt(i, 0);
                BigDecimal precioBD = (BigDecimal) tablaDetalles.getValueAt(i, 2);
                Integer cantidad = Integer.valueOf(tablaDetalles.getValueAt(i, 3).toString());
                BigDecimal subtotalBD = (BigDecimal) tablaDetalles.getValueAt(i, 4);

                // Crear DetalleFactura usando setters
                DetalleFactura detalle = new DetalleFactura();
                detalle.setIdProducto(idProducto);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(precioBD.floatValue());
                detalle.setSubtotal(subtotalBD.floatValue());

                detalles.add(detalle);
            }

            int idGenerado = facturaDAO.crearFactura(factura, detalles);
            JOptionPane.showMessageDialog(this, "Factura guardada con ID: " + idGenerado);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error en formato de datos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
package VentasDAO.UI.entities;

import VentasDAO.DAO.*;
import VentasDAO.Objetos.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
    private final JLabel lblTotal = new JLabel("$ 0.00");
    private final JLabel lblFecha = new JLabel();
    private final JLabel lblCantidadItems = new JLabel("0 items");

    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color INFO_COLOR = new Color(142, 68, 173);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    public FacturaFrame(java.awt.Window owner) {
        super(owner, "Nueva Factura", ModalityType.APPLICATION_MODAL);
        setSize(1100, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);

        initComponents();
        configurarRenderers();
        cargarCombos();
        actualizarFecha();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(crearPanelCabecera(), BorderLayout.NORTH);
        mainPanel.add(crearPanelCentral(), BorderLayout.CENTER);
        mainPanel.add(crearPanelResumen(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel crearPanelCabecera() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(BACKGROUND_COLOR);

        // Panel de información de factura
        JPanel panelInfo = new JPanel(new GridBagLayout());
        panelInfo.setBackground(PANEL_COLOR);
        panelInfo.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        "Información de la Factura",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        PRIMARY_COLOR),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Primera fila: Número y Fecha
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        panelInfo.add(crearLabel("Número:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        txtNumero.setText("F-" + System.currentTimeMillis());
        estilizarTextField(txtNumero);
        panelInfo.add(txtNumero, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        panelInfo.add(crearLabel("Fecha:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.3;
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFecha.setForeground(TEXT_COLOR);
        panelInfo.add(lblFecha, gbc);

        // Segunda fila: Cliente
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        panelInfo.add(crearLabel("Cliente:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        estilizarComboBox(cbCliente);
        panelInfo.add(cbCliente, gbc);

        // Tercera fila: Forma de Pago y Observaciones
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        panelInfo.add(crearLabel("Forma de Pago:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        estilizarComboBox(cbFormaPago);
        panelInfo.add(cbFormaPago, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        panelInfo.add(crearLabel("Observaciones:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.7;
        estilizarTextField(txtObservaciones);
        panelInfo.add(txtObservaciones, gbc);

        panelPrincipal.add(panelInfo, BorderLayout.CENTER);

        return panelPrincipal;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        // Panel de tabla
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(PANEL_COLOR);
        panelTabla.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        "Detalles de la Factura",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        PRIMARY_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID Producto", "Nombre", "Precio", "Cantidad", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDetalles.setModel(modelo);
        tablaDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaDetalles.setRowHeight(30);
        tablaDetalles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDetalles.setSelectionBackground(new Color(174, 214, 241));
        tablaDetalles.setSelectionForeground(TEXT_COLOR);
        tablaDetalles.setGridColor(new Color(189, 195, 199));

        JTableHeader header = tablaDetalles.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(189, 195, 199));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Configurar renderizadores para alinear números a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaDetalles.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tablaDetalles.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tablaDetalles.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        // Ajustar anchos de columnas
        tablaDetalles.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaDetalles.getColumnModel().getColumn(1).setPreferredWidth(300);
        tablaDetalles.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaDetalles.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaDetalles.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(tablaDetalles);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones para productos
        JPanel panelBotonesProductos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBotonesProductos.setBackground(PANEL_COLOR);

        JButton btnAgregar = crearBoton(" Agregar Producto", SUCCESS_COLOR);
        JButton btnQuitar = crearBoton(" Quitar Producto", DANGER_COLOR);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnQuitar.addActionListener(e -> quitarProducto());

        panelBotonesProductos.add(btnAgregar);
        panelBotonesProductos.add(btnQuitar);
        panelBotonesProductos.add(lblCantidadItems);
        lblCantidadItems.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCantidadItems.setForeground(INFO_COLOR);

        panelTabla.add(panelBotonesProductos, BorderLayout.SOUTH);

        panel.add(panelTabla, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        // Panel de total
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelTotal.setBackground(PANEL_COLOR);
        panelTotal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SUCCESS_COLOR, 2),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblTotalTexto = new JLabel("TOTAL:");
        lblTotalTexto.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalTexto.setForeground(TEXT_COLOR);

        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotal.setForeground(SUCCESS_COLOR);

        panelTotal.add(lblTotalTexto);
        panelTotal.add(lblTotal);

        // Panel de botones principales
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBotones.setBackground(BACKGROUND_COLOR);

        JButton btnCancelar = crearBoton(" Cancelar", DANGER_COLOR);
        JButton btnGuardar = crearBoton(" Guardar Factura", SUCCESS_COLOR);
        btnGuardar.setPreferredSize(new Dimension(180, 40));
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarFactura());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        panel.add(panelTotal, BorderLayout.WEST);
        panel.add(panelBotones, BorderLayout.EAST);

        return panel;
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(180, 38));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    private void estilizarTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textField.setPreferredSize(new Dimension(200, 35));
    }

    private void estilizarComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(250, 35));
        comboBox.setBackground(Color.WHITE);
    }

    private void actualizarFecha() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        lblFecha.setText(sdf.format(new Date()));
    }

    private void configurarRenderers() {
        cbCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list,
                                                          Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) {
                    Cliente cliente = (Cliente) value;
                    String nombre = cliente.getNombre() != null ? cliente.getNombre() : "";
                    String apellido = cliente.getApellido() != null ? cliente.getApellido() : "";
                    setText((nombre + " " + apellido).trim());
                }
                return comp;
            }
        });

        cbFormaPago.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list,
                                                          Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof FormaPago) {
                    FormaPago formaPago = (FormaPago) value;
                    setText(formaPago.getNombre() != null ? formaPago.getNombre() : "");
                }
                return comp;
            }
        });
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
        cbCliente.setSelectedIndex(-1);
        cbFormaPago.setSelectedIndex(-1);
    }

    private void agregarProducto() {
        List<Producto> productos = productoDAO.listar();
        if (productos.isEmpty()) {
            mostrarMensaje("No hay productos disponibles", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Producto producto = (Producto) JOptionPane.showInputDialog(this,
                "Seleccione un producto",
                "Agregar Producto",
                JOptionPane.PLAIN_MESSAGE,
                null,
                productos.toArray(),
                productos.get(0));

        if (producto == null) {
            return;
        }

        if (producto.getPrecio() == null) {
            mostrarMensaje("El producto seleccionado no tiene precio configurado",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cantidadTexto = JOptionPane.showInputDialog(this,
                "Ingrese la cantidad:", "1");
        if (cantidadTexto == null || cantidadTexto.trim().isEmpty()) {
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadTexto);
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }

            BigDecimal precio = producto.getPrecio();
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
            mostrarMensaje("Cantidad inválida. Debe ser un número positivo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarProducto() {
        int fila = tablaDetalles.getSelectedRow();
        if (fila >= 0) {
            ((DefaultTableModel) tablaDetalles.getModel()).removeRow(fila);
            recalcularTotal();
        } else {
            mostrarMensaje("Debe seleccionar un producto de la lista",
                    "Selección requerida", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void recalcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        int cantidadItems = 0;

        for (int i = 0; i < tablaDetalles.getRowCount(); i++) {
            Object valor = tablaDetalles.getValueAt(i, 4);
            if (valor instanceof BigDecimal) {
                total = total.add((BigDecimal) valor);
            } else if (valor != null) {
                total = total.add(new BigDecimal(valor.toString()));
            }
            cantidadItems++;
        }

        lblTotal.setText("$ " + String.format("%.2f", total));
        lblCantidadItems.setText(cantidadItems + " item" + (cantidadItems != 1 ? "s" : ""));
    }

    private boolean validarCampos() {
        // Validar cliente
        if (cbCliente.getSelectedItem() == null) {
            mostrarMensaje("Debe seleccionar un cliente",
                    "Validación de Datos", JOptionPane.WARNING_MESSAGE);
            cbCliente.requestFocus();
            return false;
        }

        // Validar forma de pago
        if (cbFormaPago.getSelectedItem() == null) {
            mostrarMensaje("Debe seleccionar una forma de pago",
                    "Validación de Datos", JOptionPane.WARNING_MESSAGE);
            cbFormaPago.requestFocus();
            return false;
        }
        // Validar campos de texto con mínimo 3 caracteres
        if (txtObservaciones.getText().trim().length() < 3) {
            mostrarMensaje("La observacion debe tener al menos 3 caracteres",
                    "Validación de Datos", JOptionPane.WARNING_MESSAGE);
            txtObservaciones.requestFocus();
            return false;
        }

        return true;
    }

    private void guardarFactura() {
        if (!validarCampos()) {
            return;
        }
        try {
            // Validar campos básicos
            if (txtNumero.getText().trim().isEmpty() ||
                    txtObservaciones.getText().trim().isEmpty()) {
                mostrarMensaje("Número y observaciones son obligatorios",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            FormaPago formaPago = (FormaPago) cbFormaPago.getSelectedItem();

            if (cliente == null || formaPago == null) {
                mostrarMensaje("Debe seleccionar cliente y forma de pago",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (tablaDetalles.getRowCount() == 0) {
                mostrarMensaje("Debe agregar al menos un producto",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Calcular total
            BigDecimal totalBD = BigDecimal.ZERO;
            for (int i = 0; i < tablaDetalles.getRowCount(); i++) {
                Object valor = tablaDetalles.getValueAt(i, 4);
                if (valor instanceof BigDecimal) {
                    totalBD = totalBD.add((BigDecimal) valor);
                } else if (valor != null) {
                    totalBD = totalBD.add(new BigDecimal(valor.toString()));
                }
            }
            Float total = totalBD.floatValue();

            // Crear factura
            Factura factura = new Factura(
                    0,
                    txtNumero.getText().trim(),
                    new Date(),
                    total,
                    txtObservaciones.getText().trim(),
                    cliente,
                    formaPago,
                    null
            );

            // Crear detalles
            List<DetalleFactura> detalles = new ArrayList<>();
            for (int i = 0; i < tablaDetalles.getRowCount(); i++) {
                Integer idProducto = (Integer) tablaDetalles.getValueAt(i, 0);
                BigDecimal precioBD = (BigDecimal) tablaDetalles.getValueAt(i, 2);
                Integer cantidad = Integer.valueOf(tablaDetalles.getValueAt(i, 3).toString());
                BigDecimal subtotalBD = (BigDecimal) tablaDetalles.getValueAt(i, 4);

                DetalleFactura detalle = new DetalleFactura();
                detalle.setFactura(factura);
                Producto producto = new Producto();
                producto.setIdProducto(idProducto);
                detalle.setProducto(producto);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(precioBD.floatValue());
                detalle.setSubtotal(subtotalBD.floatValue());

                detalles.add(detalle);
            }

            int idGenerado = facturaDAO.crearFactura(factura, detalles);

            JOptionPane.showMessageDialog(this,
                    "✓ Factura guardada exitosamente\n\nID: " + idGenerado +
                            "\nTotal: $ " + String.format("%.2f", total),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            mostrarMensaje("Error en formato de datos numéricos",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            mostrarMensaje("Error al guardar la factura:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
}
package VentasDAO.UI.entities;

import VentasDAO.DAO.DetalleFacturaDAO;
import VentasDAO.DAO.FacturaDAO;
import VentasDAO.DAO.ProductoDAO;
import VentasDAO.Objetos.DetalleFactura;
import VentasDAO.Objetos.Factura;
import VentasDAO.Objetos.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;


public class DetalleFacturaFrame extends JDialog {

    private final DetalleFacturaDAO detalleDAO;
    private final FacturaDAO facturaDAO;
    private final ProductoDAO productoDAO;

    private JTable tabla;
    private JComboBox<Factura> cbFactura;
    private JComboBox<Producto> cbProducto;
    private JTextField txtCantidad;
    private JTextField txtPrecio;
    private JTextField txtSubtotal;

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
    private static final Color ACCENT_COLOR = new Color(26, 188, 156);

    public DetalleFacturaFrame(java.awt.Component parent) {
        this(parent, new DetalleFacturaDAO(), new FacturaDAO(), new ProductoDAO());
    }

    public DetalleFacturaFrame(Component parent, DetalleFacturaDAO detalleDAO,
                               FacturaDAO facturaDAO, ProductoDAO productoDAO) {
        super(JOptionPane.getFrameForComponent(parent),
                "Gestión de Detalles de Factura", true);
        this.detalleDAO = detalleDAO;
        this.facturaDAO = facturaDAO;
        this.productoDAO = productoDAO;

        initUI();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(crearPanelFormulario(), BorderLayout.NORTH);
        mainPanel.add(crearPanelTabla(), BorderLayout.CENTER);
        mainPanel.add(crearPanelBotones(), BorderLayout.SOUTH);

        add(mainPanel);
        aplicarFiltrosNumericos();
        configurarCombos();
        cargarCombos();
        refrescarTabla();

        setSize(1000, 650);
    }

    private void aplicarFiltrosNumericos() {
        // Filtro para Precio: solo números
        ((AbstractDocument) txtPrecio.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string != null && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text != null && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        // Filtro para Cantidad: solo números
        ((AbstractDocument) txtCantidad.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string != null && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text != null && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        "Datos del Detalle",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        PRIMARY_COLOR),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Primera fila: Factura
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        panel.add(crearLabel("Factura:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        cbFactura = new JComboBox<>();
        cbFactura.setPrototypeDisplayValue(new Factura());
        estilizarComboBox(cbFactura);
        panel.add(cbFactura, gbc);

        // Segunda fila: Producto
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        panel.add(crearLabel("Producto:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        cbProducto = new JComboBox<>();
        estilizarComboBox(cbProducto);
        cbProducto.addActionListener(e -> completarPrecioDesdeProducto());
        panel.add(cbProducto, gbc);

        // Tercera fila: Cantidad, Precio y Subtotal
        gbc.gridwidth = 1;
        gbc.gridy = 2;

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        panel.add(crearLabel("Cantidad:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        txtCantidad = new JTextField(10);
        estilizarTextField(txtCantidad);
        txtCantidad.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { recalcularSubtotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { recalcularSubtotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { recalcularSubtotal(); }
        });
        panel.add(txtCantidad, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        panel.add(crearLabel("Precio Unitario:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.3;
        txtPrecio = new JTextField(10);
        estilizarTextField(txtPrecio);
        txtPrecio.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { recalcularSubtotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { recalcularSubtotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { recalcularSubtotal(); }
        });
        panel.add(txtPrecio, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.0;
        panel.add(crearLabel("Subtotal:"), gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.4;
        txtSubtotal = new JTextField(12);
        txtSubtotal.setEditable(false);
        txtSubtotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtSubtotal.setForeground(SUCCESS_COLOR);
        txtSubtotal.setBackground(new Color(232, 245, 233));
        txtSubtotal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SUCCESS_COLOR, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtSubtotal.setPreferredSize(new Dimension(120, 35));
        txtSubtotal.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(txtSubtotal, gbc);

        return panel;
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        "Lista de Detalles de Factura",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        PRIMARY_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        tabla = new JTable();
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(30);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(new Color(174, 214, 241));
        tabla.setSelectionForeground(TEXT_COLOR);
        tabla.setGridColor(new Color(189, 195, 199));
        tabla.setShowGrid(true);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(189, 195, 199));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JButton btnLimpiar = crearBoton(" Limpiar", WARNING_COLOR);
        JButton btnEditar = crearBoton(" Editar", SECONDARY_COLOR);
        JButton btnEliminar = crearBoton(" Eliminar", DANGER_COLOR);
        JButton btnGuardar = crearBoton(" Guardar", SUCCESS_COLOR);

        btnLimpiar.addActionListener(e -> limpiar());
        btnEditar.addActionListener(e -> cargarSeleccion());
        btnEliminar.addActionListener(e -> eliminar());
        btnGuardar.addActionListener(e -> guardar());

        panel.add(btnLimpiar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnGuardar);

        return panel;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(130, 38));
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
        textField.setPreferredSize(new Dimension(150, 35));
    }

    private void estilizarComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(300, 35));
        comboBox.setBackground(Color.WHITE);
    }

    private void configurarRenderers() {
        // Renderizador personalizado para Factura
        tabla.setDefaultRenderer(Factura.class, new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Factura) {
                    Factura factura = (Factura) value;
                    String numero = factura.getNumeroFactura();
                    if (numero == null || numero.trim().isEmpty()) {
                        numero = "Factura #" + factura.getIdFactura();
                    }
                    setText(numero);
                } else {
                    super.setValue(value);
                }
            }
        });

        // Renderizador personalizado para Producto
        tabla.setDefaultRenderer(Producto.class, new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Producto) {
                    Producto producto = (Producto) value;
                    String nombre = producto.getNombre();
                    if (nombre == null || nombre.trim().isEmpty()) {
                        nombre = "Producto #" + producto.getIdProducto();
                    }
                    setText(nombre);
                } else {
                    super.setValue(value);
                }
            }
        });
    }

    private void configurarRenderersNumericos() {
        if (tabla.getColumnCount() >= 6) {
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

            // Columna 3: Cantidad
            // Columna 4: Precio Unitario
            // Columna 5: Subtotal
            tabla.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
            tabla.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
            tabla.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        }
    }

    private void configurarCombos() {
        cbFactura.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list,
                                                          Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof Factura) {
                    Factura factura = (Factura) value;
                    String numero = factura.getNumeroFactura();
                    if (numero == null || numero.trim().isEmpty()) {
                        numero = "Factura #" + factura.getIdFactura();
                    }
                    setText(numero);
                }
                return comp;
            }
        });

        cbProducto.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list,
                                                          Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof Producto) {
                    Producto producto = (Producto) value;
                    String nombre = producto.getNombre();
                    if (nombre == null || nombre.trim().isEmpty()) {
                        nombre = "Producto #" + producto.getIdProducto();
                    }
                    setText(nombre);
                }
                return comp;
            }
        });
    }

    private void cargarCombos() {
        cbFactura.removeAllItems();
        for (Factura factura : facturaDAO.listar()) {
            cbFactura.addItem(factura);
        }
        cbProducto.removeAllItems();
        for (Producto producto : productoDAO.listar()) {
            cbProducto.addItem(producto);
        }
        cbFactura.setSelectedIndex(-1);
        cbProducto.setSelectedIndex(-1);
    }

    private void completarPrecioDesdeProducto() {
        Producto producto = (Producto) cbProducto.getSelectedItem();
        if (producto != null && producto.getPrecio() != null &&
                txtPrecio.getText().trim().isEmpty()) {
            txtPrecio.setText(producto.getPrecio().toString());
            recalcularSubtotal();
        }
    }

    private DefaultTableModel crearModeloTabla() {
        return new DefaultTableModel(
                new Object[]{"ID", "Factura", "Producto", "Cantidad", "Precio", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class;
                    case 1: return Factura.class;
                    case 2: return Producto.class;
                    case 3: return Integer.class;
                    case 4:
                    case 5: return Float.class;
                    default: return Object.class;
                }
            }
        };
    }

    private void refrescarTabla() {
        DefaultTableModel modelo = crearModeloTabla();
        for (DetalleFactura detalle : detalleDAO.listar()) {
            modelo.addRow(new Object[]{
                    detalle.getIdDetalle(),
                    detalle.getFactura(),
                    detalle.getProducto(),
                    detalle.getCantidad(),
                    detalle.getPrecioUnitario(),
                    detalle.getSubtotal()
            });
        }
        tabla.setModel(modelo);

        // Configurar renderers DESPUÉS de asignar el modelo
        configurarRenderers();
        configurarRenderersNumericos();

        // Ajustar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(250);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(120);
    }

    private void limpiar() {
        cbFactura.setSelectedIndex(-1);
        cbProducto.setSelectedIndex(-1);
        txtCantidad.setText("");
        txtPrecio.setText("");
        txtSubtotal.setText("");
        tabla.clearSelection();
        cbFactura.requestFocus();
    }

    private void recalcularSubtotal() {
        try {
            String cantidadStr = txtCantidad.getText().trim();
            String precioStr = txtPrecio.getText().trim().replace(",", ".");

            if (cantidadStr.isEmpty() || precioStr.isEmpty()) {
                txtSubtotal.setText("");
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);
            BigDecimal precio = new BigDecimal(precioStr);
            BigDecimal subtotal = precio.multiply(new BigDecimal(cantidad));
            txtSubtotal.setText("$ " + subtotal.toPlainString());
        } catch (NumberFormatException ex) {
            txtSubtotal.setText("");
        }
    }

    private DetalleFactura leerFormulario(Integer idExistente) {
        Factura factura = (Factura) cbFactura.getSelectedItem();
        Producto producto = (Producto) cbProducto.getSelectedItem();

        if (factura == null || producto == null) {
            mostrarMensaje("Debe seleccionar una factura y un producto",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String cantidadStr = txtCantidad.getText().trim();
        String precioStr = txtPrecio.getText().trim().replace(",", ".");

        if (cantidadStr.isEmpty() || precioStr.isEmpty()) {
            mostrarMensaje("Cantidad y precio son obligatorios",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            float precio = Float.parseFloat(precioStr);
            float subtotal = cantidad * precio;

            DetalleFactura detalle = new DetalleFactura();
            if (idExistente != null) {
                detalle.setIdDetalle(idExistente);
            }
            detalle.setFactura(factura);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(subtotal);
            return detalle;
        } catch (NumberFormatException ex) {
            mostrarMensaje("Cantidad o precio inválidos", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private boolean validarCampos() {

        // Validar Factura seleccionada
        if (cbFactura.getSelectedItem() == null) {
            mostrarMensaje("Debe seleccionar una factura",
                    "Validación de Datos", JOptionPane.WARNING_MESSAGE);
            cbFactura.requestFocus();
            return false;
        }
        // Validar Producto seleccionado
        if (cbProducto.getSelectedItem() == null) {
            mostrarMensaje("Debe seleccionar un producto",
                    "Validación de Datos", JOptionPane.WARNING_MESSAGE);
            cbProducto.requestFocus();
            return false;
        }

        if (txtCantidad.getText().trim().isEmpty()) {
            mostrarMensaje("La cantidad es obligatorio",
                    "Validación de Datos", JOptionPane.WARNING_MESSAGE);
            txtCantidad.requestFocus();
            return false;
        }

        // Validar que cantidad sea un número válido mayor a 0
        try {
            long stock = Long.parseLong(txtCantidad.getText().trim());
            if (stock <= 0) {
                mostrarMensaje("La cantidad debe ser un número mayor a 0",
                        "Validación de Datos", JOptionPane.WARNING_MESSAGE);
                txtCantidad.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("La cantidad debe contener solo números",
                    "Validación de Datos", JOptionPane.WARNING_MESSAGE);
            txtCantidad.requestFocus();
            return false;
        }
        // Validar campos numéricos
        if (txtPrecio.getText().trim().isEmpty()) {
            mostrarMensaje("El precio es obligatorio",
                    "Validación de Datos", JOptionPane.WARNING_MESSAGE);
            txtPrecio.requestFocus();
            return false;
        }

        // Validar que precio sea un número válido mayor a 0
        try {
            long precio = Long.parseLong(txtPrecio.getText().trim());
            if (precio <= 0) {
                mostrarMensaje("El precio debe ser un número mayor a 0",
                        "Validación de Datos", JOptionPane.WARNING_MESSAGE);
                txtPrecio.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("El precio debe contener solo números",
                    "Validación de Datos", JOptionPane.WARNING_MESSAGE);
            txtPrecio.requestFocus();
            return false;
        }
        return true;
    }

    private void guardar() {
        if (!validarCampos()) {
            return;
        }
        int filaSeleccionada = tabla.getSelectedRow();
        Integer idExistente = null;
        if (filaSeleccionada >= 0) {
            Object valorId = tabla.getValueAt(filaSeleccionada, 0);
            if (valorId instanceof Integer) {
                idExistente = (Integer) valorId;
            }
        }

        DetalleFactura detalle = leerFormulario(idExistente);
        if (detalle == null) {
            return;
        }

        try {
            if (idExistente == null) {
                detalleDAO.insertar(detalle);
                mostrarMensaje("Detalle guardado exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                detalleDAO.actualizar(detalle);
                mostrarMensaje("Detalle actualizado exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            limpiar();
            refrescarTabla();
        } catch (SQLException ex) {
            mostrarMensaje("Error al guardar: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Debe seleccionar un detalle de la tabla",
                    "Selección requerida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer id = (Integer) tabla.getValueAt(fila, 0);
        Factura factura = (Factura) tabla.getValueAt(fila, 1);
        Producto producto = (Producto) tabla.getValueAt(fila, 2);
        Integer cantidad = (Integer) tabla.getValueAt(fila, 3);
        Float precio = (Float) tabla.getValueAt(fila, 4);
        Float subtotal = (Float) tabla.getValueAt(fila, 5);

        cbFactura.setSelectedItem(factura);
        cbProducto.setSelectedItem(producto);
        txtCantidad.setText(String.valueOf(cantidad));
        txtPrecio.setText(String.valueOf(precio));
        txtSubtotal.setText("$ " + String.valueOf(subtotal));
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Debe seleccionar un detalle de la tabla",
                    "Selección requerida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer id = (Integer) tabla.getValueAt(fila, 0);
        if (id == null) {
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el detalle #" + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                detalleDAO.eliminar(id);
                mostrarMensaje("Detalle eliminado exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiar();
                refrescarTabla();
            } catch (SQLException ex) {
                mostrarMensaje("Error al eliminar: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
}
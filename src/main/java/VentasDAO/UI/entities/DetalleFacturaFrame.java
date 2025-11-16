package VentasDAO.UI.entities;

import VentasDAO.DAO.DetalleFacturaDAO;
import VentasDAO.DAO.FacturaDAO;
import VentasDAO.DAO.ProductoDAO;
import VentasDAO.Objetos.DetalleFactura;
import VentasDAO.Objetos.Factura;
import VentasDAO.Objetos.Producto;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

    public DetalleFacturaFrame(java.awt.Component parent) {
        this(
                parent,
                new DetalleFacturaDAO(),
                new FacturaDAO(),
                new ProductoDAO()
        );
    }

    public DetalleFacturaFrame(Component parent, DetalleFacturaDAO detalleDAO,
                               FacturaDAO facturaDAO, ProductoDAO productoDAO) {
        super(JOptionPane.getFrameForComponent(parent), "Gestión de Detalles de Factura", true);
        this.detalleDAO = detalleDAO;
        this.facturaDAO = facturaDAO;
        this.productoDAO = productoDAO;
        initUI();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        panelFormulario.add(new JLabel("Factura:"), gbc);
        gbc.gridx++;
        cbFactura = new JComboBox<>();
        cbFactura.setPrototypeDisplayValue(new Factura());
        panelFormulario.add(cbFactura, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelFormulario.add(new JLabel("Producto:"), gbc);
        gbc.gridx++;
        cbProducto = new JComboBox<>();
        panelFormulario.add(cbProducto, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelFormulario.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx++;
        txtCantidad = new JTextField(10);
        panelFormulario.add(txtCantidad, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelFormulario.add(new JLabel("Precio unitario:"), gbc);
        gbc.gridx++;
        txtPrecio = new JTextField(10);
        panelFormulario.add(txtPrecio, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panelFormulario.add(new JLabel("Subtotal:"), gbc);
        gbc.gridx++;
        txtSubtotal = new JTextField(10);
        txtSubtotal.setEditable(false);
        panelFormulario.add(txtSubtotal, gbc);

        add(panelFormulario, BorderLayout.NORTH);

        // Tabla
        tabla = new JTable();
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> cargarSeleccion());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        cbProducto.addActionListener(e -> completarPrecioDesdeProducto());

        // ✅ ORDEN CORRECTO: Primero configurar combos, luego cargar datos,
        // y finalmente refrescar tabla (que asigna el modelo y configura renderers)
        configurarCombos();
        cargarCombos();
        refrescarTabla();  // Esto ahora configura el modelo Y los renderers

        pack();
    }

    private DefaultTableModel crearModeloTabla() {
        return new DefaultTableModel(
                new Object[]{"ID", "Factura", "Producto", "Cantidad", "Precio", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return Factura.class;
                    case 2:
                        return Producto.class;
                    case 3:
                        return Integer.class;
                    case 4:
                    case 5:
                        return Float.class;
                    default:
                        return Object.class;
                }
            }
        };
    }

    // ✅ MÉTODO MOVIDO: Ahora se llama desde refrescarTabla() DESPUÉS de asignar el modelo
    private void configurarRenderers() {
        // Verificar que la tabla tiene modelo y columnas
        if (tabla.getModel() == null || tabla.getColumnCount() == 0) {
            return;
        }

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

        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);

        // ✅ AHORA SÍ hay columnas disponibles
        if (tabla.getColumnCount() > 3) {
            tabla.getColumnModel().getColumn(3).setCellRenderer(derecha);
        }
        if (tabla.getColumnCount() > 4) {
            tabla.getColumnModel().getColumn(4).setCellRenderer(derecha);
        }
        if (tabla.getColumnCount() > 5) {
            tabla.getColumnModel().getColumn(5).setCellRenderer(derecha);
        }
    }

    private void configurarCombos() {
        cbFactura.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );
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
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );
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
        if (producto != null
                && producto.getPrecio() != null
                && txtPrecio.getText().trim().isEmpty()) {
            txtPrecio.setText(producto.getPrecio().toString());
            recalcularSubtotal();
        }
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

        // ✅ AHORA configuramos los renderers DESPUÉS de asignar el modelo
        configurarRenderers();
    }

    private void limpiar() {
        cbFactura.setSelectedIndex(-1);
        cbProducto.setSelectedIndex(-1);
        txtCantidad.setText("");
        txtPrecio.setText("");
        txtSubtotal.setText("");
        tabla.clearSelection();
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
            txtSubtotal.setText(subtotal.toPlainString());
        } catch (NumberFormatException ex) {
            txtSubtotal.setText("");
        }
    }

    private DetalleFactura leerFormulario(Integer idExistente) {
        Factura factura = (Factura) cbFactura.getSelectedItem();
        Producto producto = (Producto) cbProducto.getSelectedItem();

        if (factura == null || producto == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una factura y un producto",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String cantidadStr = txtCantidad.getText().trim();
        String precioStr = txtPrecio.getText().trim().replace(",", ".");

        if (cantidadStr.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Cantidad y precio son obligatorios",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this,
                    "Cantidad o precio inválidos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void guardar() {
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
            } else {
                detalleDAO.actualizar(detalle);
            }
            limpiar();
            refrescarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
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
        txtSubtotal.setText(String.valueOf(subtotal));
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        Integer id = (Integer) tabla.getValueAt(fila, 0);
        if (id == null) {
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar detalle " + id + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                detalleDAO.eliminar(id);
                limpiar();
                refrescarTabla();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
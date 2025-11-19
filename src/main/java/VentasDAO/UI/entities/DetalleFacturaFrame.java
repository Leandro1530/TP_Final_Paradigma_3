package VentasDAO.UI.entities;

import VentasDAO.DAO.ClienteDAO;
import VentasDAO.DAO.DetalleFacturaDAO;
import VentasDAO.DAO.FacturaDAO;
import VentasDAO.DAO.FormaPagoDAO;
import VentasDAO.Objetos.Cliente;
import VentasDAO.Objetos.DetalleFactura;
import VentasDAO.Objetos.Factura;
import VentasDAO.Objetos.FormaPago;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DetalleFacturaFrame extends JDialog {

    private final FacturaDAO facturaDAO;
    private final DetalleFacturaDAO detalleFacturaDAO;
    private final ClienteDAO clienteDAO;
    private final FormaPagoDAO formaPagoDAO;

    private final JTable tablaFacturas;
    private final JTable tablaDetalles;

    // Mapas para cachear datos y evitar consultas repetidas
    private Map<Integer, Cliente> cacheClientes;
    private Map<Integer, FormaPago> cacheFormasPago;

    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);


    public DetalleFacturaFrame(java.awt.Component parent) {
        super(JOptionPane.getFrameForComponent(parent),
                "Registro de Facturas y Detalles", true);

        // Inicializar DAOs
        this.facturaDAO = new FacturaDAO();
        this.detalleFacturaDAO = new DetalleFacturaDAO();
        this.clienteDAO = new ClienteDAO();
        this.formaPagoDAO = new FormaPagoDAO();

        // Inicializar tablas
        this.tablaFacturas = new JTable();
        this.tablaDetalles = new JTable();

        // Inicializar caches
        this.cacheClientes = new HashMap<>();
        this.cacheFormasPago = new HashMap<>();

        initUI();
        configurarEventos();
        cargarCaches();
        cargarFacturas();

        setLocationRelativeTo(parent);
    }


    private void initUI() {
        setSize(1200, 750);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Crear JSplitPane vertical para maestro-detalle
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.45);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(8);
        splitPane.setBackground(BACKGROUND_COLOR);

        // Panel superior: Tabla de Facturas (maestro)
        splitPane.setTopComponent(crearPanelFacturas());

        // Panel inferior: Tabla de Detalles (detalle)
        splitPane.setBottomComponent(crearPanelDetalles());

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Panel de botones
        mainPanel.add(crearPanelBotones(), BorderLayout.SOUTH);

        add(mainPanel);
    }


    private JPanel crearPanelFacturas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        "Facturas emitidas (cabecera)",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        PRIMARY_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Configurar tabla de facturas
        tablaFacturas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaFacturas.setRowHeight(28);
        tablaFacturas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaFacturas.setSelectionBackground(new Color(174, 214, 241));
        tablaFacturas.setSelectionForeground(TEXT_COLOR);
        tablaFacturas.setGridColor(new Color(189, 195, 199));
        tablaFacturas.setShowGrid(true);

        // Estilizar encabezado
        JTableHeader header = tablaFacturas.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(189, 195, 199));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        JScrollPane scrollPane = new JScrollPane(tablaFacturas);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }


    private JPanel crearPanelDetalles() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "Detalle de la factura seleccionada",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        SECONDARY_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Configurar tabla de detalles
        tablaDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaDetalles.setRowHeight(28);
        tablaDetalles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDetalles.setSelectionBackground(new Color(174, 214, 241));
        tablaDetalles.setSelectionForeground(TEXT_COLOR);
        tablaDetalles.setGridColor(new Color(189, 195, 199));
        tablaDetalles.setShowGrid(true);

        // Estilizar encabezado
        JTableHeader header = tablaDetalles.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(189, 195, 199));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        JScrollPane scrollPane = new JScrollPane(tablaDetalles);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }


    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JButton btnRefrescar = crearBoton(" Refrescar", SUCCESS_COLOR);
        JButton btnCerrar = crearBoton(" Cerrar", WARNING_COLOR);

        btnRefrescar.addActionListener(e -> {
            cargarCaches();
            cargarFacturas();
            limpiarDetalles();
        });

        btnCerrar.addActionListener(e -> dispose());

        panel.add(btnRefrescar);
        panel.add(btnCerrar);

        return panel;
    }


    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(150, 40));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
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


    private void configurarEventos() {
        tablaFacturas.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (!e.getValueIsAdjusting()) {
                            cargarDetallesDeFacturaSeleccionada();
                        }
                    }
                }
        );
    }


    private void cargarCaches() {
        cacheClientes.clear();
        cacheFormasPago.clear();

        // Cargar clientes
        List<Cliente> clientes = clienteDAO.listar();
        for (Cliente cliente : clientes) {
            if (cliente.getIdCliente() != null) {
                cacheClientes.put(cliente.getIdCliente(), cliente);
            }
        }

        // Cargar formas de pago
        List<FormaPago> formasPago = formaPagoDAO.listar();
        for (FormaPago fp : formasPago) {
            cacheFormasPago.put(fp.getIdFormaPago(), fp);
        }
    }


    private void cargarFacturas() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "N° Factura", "Fecha", "Cliente", "Forma pago", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                if (columnIndex == 5) return Float.class;
                return String.class;
            }
        };

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Usar FacturaDAO para obtener las facturas
        List<Factura> facturas = facturaDAO.listar();

        for (Factura factura : facturas) {
            // ID
            Integer idFactura = factura.getIdFactura();

            // N° Factura
            String numeroFactura = factura.getNumeroFactura();
            if (numeroFactura == null || numeroFactura.trim().isEmpty()) {
                numeroFactura = "Factura #" + idFactura;
            }

            // Fecha
            String fecha = "-";
            Date fechaGeneracion = factura.getFechaGeneracion();
            if (fechaGeneracion != null) {
                fecha = sdf.format(fechaGeneracion);
            }

            // Cliente - buscar en caché o usar el objeto que viene del DAO
            String nombreCliente = "-";
            Cliente cliente = factura.getCliente();
            if (cliente != null && cliente.getIdCliente() != null) {
                // Buscar datos completos del cliente en caché
                Cliente clienteCompleto = cacheClientes.get(cliente.getIdCliente());
                if (clienteCompleto != null) {
                    String nombre = clienteCompleto.getNombre() != null ? clienteCompleto.getNombre() : "";
                    String apellido = clienteCompleto.getApellido() != null ? clienteCompleto.getApellido() : "";
                    nombreCliente = (nombre + " " + apellido).trim();
                    if (nombreCliente.isEmpty()) {
                        nombreCliente = "Cliente ID: " + cliente.getIdCliente();
                    }
                } else {
                    nombreCliente = "Cliente ID: " + cliente.getIdCliente();
                }
            }

            // Forma de pago - buscar en caché o usar el objeto que viene del DAO
            String formaPago = "-";
            FormaPago fp = factura.getFormapago();
            if (fp != null && fp.getIdFormaPago() > 0) {
                // Buscar datos completos en caché
                FormaPago fpCompleta = cacheFormasPago.get(fp.getIdFormaPago());
                if (fpCompleta != null && fpCompleta.getNombre() != null) {
                    formaPago = fpCompleta.getNombre();
                } else {
                    formaPago = "Forma Pago ID: " + fp.getIdFormaPago();
                }
            }

            // Total
            Float total = factura.getTotal();

            modelo.addRow(new Object[]{
                    idFactura,
                    numeroFactura,
                    fecha,
                    nombreCliente,
                    formaPago,
                    total
            });
        }

        tablaFacturas.setModel(modelo);

        // Configurar renderizador para alinear el total a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaFacturas.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        // Ajustar anchos de columnas
        tablaFacturas.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaFacturas.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaFacturas.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaFacturas.getColumnModel().getColumn(3).setPreferredWidth(250);
        tablaFacturas.getColumnModel().getColumn(4).setPreferredWidth(150);
        tablaFacturas.getColumnModel().getColumn(5).setPreferredWidth(120);
    }


    private void limpiarDetalles() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Producto", "Cantidad", "Precio unitario", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Integer.class;
                if (columnIndex == 2 || columnIndex == 3) return Float.class;
                return String.class;
            }
        };
        tablaDetalles.setModel(modelo);
    }


    private void cargarDetallesDeFacturaSeleccionada() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();

        if (filaSeleccionada < 0) {
            limpiarDetalles();
            return;
        }

        // Obtener el ID de la factura seleccionada
        Integer idFactura = (Integer) tablaFacturas.getValueAt(filaSeleccionada, 0);

        if (idFactura == null || idFactura <= 0) {
            limpiarDetalles();
            return;
        }

        // Crear modelo para detalles
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Producto", "Cantidad", "Precio unitario", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Integer.class;
                if (columnIndex == 2 || columnIndex == 3) return Float.class;
                return String.class;
            }
        };

        // Usar DetalleFacturaDAO para obtener los detalles de la factura
        List<DetalleFactura> detalles = detalleFacturaDAO.listarPorFactura(idFactura);

        for (DetalleFactura detalle : detalles) {
            // Producto - el DAO ya trae el nombre mediante JOIN
            String nombreProducto = "-";
            if (detalle.getProducto() != null && detalle.getProducto().getNombre() != null) {
                nombreProducto = detalle.getProducto().getNombre();
            }

            // Cantidad
            Integer cantidad = detalle.getCantidad();

            // Precio unitario
            Float precioUnitario = detalle.getPrecioUnitario();

            // Subtotal
            Float subtotal = detalle.getSubtotal();

            modelo.addRow(new Object[]{
                    nombreProducto,
                    cantidad,
                    precioUnitario,
                    subtotal
            });
        }

        tablaDetalles.setModel(modelo);

        // Configurar renderizadores para alinear números a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaDetalles.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        tablaDetalles.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tablaDetalles.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        // Ajustar anchos de columnas
        tablaDetalles.getColumnModel().getColumn(0).setPreferredWidth(400);
        tablaDetalles.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaDetalles.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaDetalles.getColumnModel().getColumn(3).setPreferredWidth(150);
    }
}
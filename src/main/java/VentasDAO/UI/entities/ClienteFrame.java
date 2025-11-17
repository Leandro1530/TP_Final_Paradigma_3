package VentasDAO.UI.entities;

import VentasDAO.DAO.ClienteDAO;
import VentasDAO.DAO.TipoClienteDAO;
import VentasDAO.Objetos.Cliente;
import VentasDAO.Objetos.TipoCliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;

/**
 * ABM mejorado para {@link Cliente} con diseño moderno y profesional.
 */
public class ClienteFrame extends JDialog {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();

    private final JTable tablaClientes = new JTable();
    private final JTextField txtNombre = new JTextField(15);
    private final JTextField txtApellido = new JTextField(15);
    private final JTextField txtDni = new JTextField(15);
    private final JTextField txtDireccion = new JTextField(15);
    private final JTextField txtTelefono = new JTextField(15);
    private final JTextField txtEmail = new JTextField(15);
    private final JComboBox<TipoCliente> cbTipo = new JComboBox<>();

    private Integer idSeleccionado;

    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    public ClienteFrame(java.awt.Window owner) {
        super(owner, "Gestión de Clientes", ModalityType.APPLICATION_MODAL);
        setSize(1100, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);

        initComponents();
        cargarTipos();
        refrescarTabla();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(crearPanelFormulario(), BorderLayout.NORTH);
        mainPanel.add(crearPanelTabla(), BorderLayout.CENTER);
        mainPanel.add(crearPanelBotones(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        "Datos del Cliente",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        PRIMARY_COLOR),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Primera fila: Nombre y Apellido
        agregarCampo(panel, gbc, "Nombre:", txtNombre, 0, 0);
        agregarCampo(panel, gbc, "Apellido:", txtApellido, 2, 0);

        // Segunda fila: DNI y Dirección
        agregarCampo(panel, gbc, "DNI:", txtDni, 0, 1);
        agregarCampo(panel, gbc, "Dirección:", txtDireccion, 2, 1);

        // Tercera fila: Teléfono y Email
        agregarCampo(panel, gbc, "Teléfono:", txtTelefono, 0, 2);
        agregarCampo(panel, gbc, "Email:", txtEmail, 2, 2);

        // Cuarta fila: Tipo Cliente
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        JLabel lblTipo = crearLabel("Tipo Cliente:");
        panel.add(lblTipo, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        estilizarComboBox(cbTipo);
        panel.add(cbTipo, gbc);

        return panel;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc,
                              String labelText, JTextField textField, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        panel.add(crearLabel(labelText), gbc);

        gbc.gridx = x + 1;
        gbc.weightx = 1.0;
        estilizarTextField(textField);
        panel.add(textField, gbc);
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
                        "Lista de Clientes",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        PRIMARY_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaClientes.setRowHeight(28);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.setSelectionBackground(new Color(174, 214, 241));
        tablaClientes.setSelectionForeground(TEXT_COLOR);
        tablaClientes.setGridColor(new Color(189, 195, 199));

        JTableHeader header = tablaClientes.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(189, 195, 199));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JButton btnLimpiar = crearBoton("Limpiar", WARNING_COLOR);
        JButton btnEditar = crearBoton("Editar", SECONDARY_COLOR);
        JButton btnEliminar = crearBoton("Eliminar", DANGER_COLOR);
        JButton btnGuardar = crearBoton("Guardar", SUCCESS_COLOR);

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
        boton.setPreferredSize(new Dimension(110, 35));
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
        textField.setPreferredSize(new Dimension(180, 35));
    }

    private void estilizarComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(200, 35));
        comboBox.setBackground(Color.WHITE);
    }

    private void cargarTipos() {
        cbTipo.removeAllItems();
        for (TipoCliente tipo : tipoClienteDAO.listar()) {
            cbTipo.addItem(tipo);
        }
        cbTipo.setSelectedIndex(-1);
    }

    private void refrescarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Apellido", "DNI", "Dirección",
                        "Teléfono", "Email", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Cliente cliente : clienteDAO.listar()) {
            modelo.addRow(new Object[]{
                    cliente.getIdCliente(),
                    cliente.getNombre(),
                    cliente.getApellido(),
                    cliente.getDni(),
                    cliente.getDireccion(),
                    cliente.getTelefono(),
                    cliente.getEmail(),
                    cliente.getTipoCliente()
            });
        }
        tablaClientes.setModel(modelo);

        // Ajustar anchos de columnas
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaClientes.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaClientes.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaClientes.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaClientes.getColumnModel().getColumn(4).setPreferredWidth(180);
        tablaClientes.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablaClientes.getColumnModel().getColumn(6).setPreferredWidth(180);
        tablaClientes.getColumnModel().getColumn(7).setPreferredWidth(100);
    }

    private void limpiar() {
        idSeleccionado = null;
        txtNombre.setText("");
        txtApellido.setText("");
        txtDni.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        cbTipo.setSelectedIndex(-1);
        tablaClientes.clearSelection();
        txtNombre.requestFocus();
    }

    private void guardar() {
        if (!validarCampos()) {
            return;
        }

        TipoCliente seleccionado = (TipoCliente) cbTipo.getSelectedItem();

        Cliente cliente = new Cliente();
        cliente.setIdCliente(idSeleccionado != null ? idSeleccionado : 0);
        cliente.setNombre(txtNombre.getText().trim());
        cliente.setApellido(txtApellido.getText().trim());
        cliente.setDni(txtDni.getText().trim());
        cliente.setDireccion(txtDireccion.getText().trim());
        cliente.setTelefono(txtTelefono.getText().trim());
        cliente.setEmail(txtEmail.getText().trim());
        cliente.setTipoCliente(seleccionado);

        try {
            if (idSeleccionado == null) {
                clienteDAO.insertar(cliente);
                mostrarMensaje("Cliente guardado exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                clienteDAO.actualizar(cliente);
                mostrarMensaje("Cliente actualizado exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            limpiar();
            refrescarTabla();
        } catch (SQLException ex) {
            mostrarMensaje("Error al guardar: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtApellido.getText().trim().isEmpty() ||
                txtDni.getText().trim().isEmpty() ||
                txtDireccion.getText().trim().isEmpty() ||
                txtTelefono.getText().trim().isEmpty() ||
                txtEmail.getText().trim().isEmpty()) {
            mostrarMensaje("Todos los campos del formulario son obligatorios",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (cbTipo.getSelectedItem() == null) {
            mostrarMensaje("Debe seleccionar un tipo de cliente",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void cargarSeleccion() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Debe seleccionar un cliente de la tabla",
                    "Selección requerida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        idSeleccionado = (Integer) tablaClientes.getValueAt(fila, 0);
        txtNombre.setText(String.valueOf(tablaClientes.getValueAt(fila, 1)));
        txtApellido.setText(String.valueOf(tablaClientes.getValueAt(fila, 2)));
        txtDni.setText(String.valueOf(tablaClientes.getValueAt(fila, 3)));
        txtDireccion.setText(String.valueOf(tablaClientes.getValueAt(fila, 4)));
        txtTelefono.setText(String.valueOf(tablaClientes.getValueAt(fila, 5)));
        txtEmail.setText(String.valueOf(tablaClientes.getValueAt(fila, 6)));

        TipoCliente tipo = (TipoCliente) tablaClientes.getValueAt(fila, 7);
        if (tipo != null) {
            for (int i = 0; i < cbTipo.getItemCount(); i++) {
                TipoCliente item = cbTipo.getItemAt(i);
                if (item != null && item.getIdTipoCliente() == tipo.getIdTipoCliente()) {
                    cbTipo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void eliminar() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Debe seleccionar un cliente de la tabla",
                    "Selección requerida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer id = (Integer) tablaClientes.getValueAt(fila, 0);
        String nombre = tablaClientes.getValueAt(fila, 1) + " " +
                tablaClientes.getValueAt(fila, 2);

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el cliente '" + nombre + "'?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                clienteDAO.eliminar(id);
                mostrarMensaje("Cliente eliminado exitosamente", "Éxito",
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
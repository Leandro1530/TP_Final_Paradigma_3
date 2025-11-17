package VentasDAO.UI.entities;

import VentasDAO.DAO.CategoriaDAO;
import VentasDAO.Objetos.Categoria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;


public class CategoriaFrame extends JDialog {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private final JTable tabla = new JTable();
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtDescripcion = new JTextField(20);
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

    public CategoriaFrame(java.awt.Window owner) {
        super(owner, "Gestión de Categorías", ModalityType.APPLICATION_MODAL);
        setSize(850, 550);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);

        initComponents();
        refrescarTabla();
    }

    private void initComponents() {
        // Panel principal con márgenes
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel de formulario
        mainPanel.add(crearPanelFormulario(), BorderLayout.NORTH);

        // Panel de tabla
        mainPanel.add(crearPanelTabla(), BorderLayout.CENTER);

        // Panel de botones
        mainPanel.add(crearPanelBotones(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        "Datos de la Categoría",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        PRIMARY_COLOR),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombre.setForeground(TEXT_COLOR);
        panel.add(lblNombre, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        estilizarTextField(txtNombre);
        panel.add(txtNombre, gbc);

        // Descripción
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDescripcion.setForeground(TEXT_COLOR);
        panel.add(lblDescripcion, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        estilizarTextField(txtDescripcion);
        panel.add(txtDescripcion, gbc);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        "Lista de Categorías",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        PRIMARY_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Configurar tabla
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(30);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(new Color(174, 214, 241));
        tabla.setSelectionForeground(TEXT_COLOR);
        tabla.setGridColor(new Color(189, 195, 199));
        tabla.setShowGrid(true);

        // Estilizar encabezado
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(189, 195, 199));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Centrar contenido de las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabla.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JButton btnLimpiar = crearBoton(" Limpiar", WARNING_COLOR);
        JButton btnEditar = crearBoton("️ Editar", SECONDARY_COLOR);
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
        boton.setPreferredSize(new Dimension(140, 40));
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

    private void estilizarTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textField.setPreferredSize(new Dimension(200, 35));
    }

    private void refrescarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Descripción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Categoria categoria : categoriaDAO.listar()) {
            modelo.addRow(new Object[]{
                    categoria.getIdCategoria(),
                    categoria.getNombre(),
                    categoria.getDescripcion()
            });
        }
        tabla.setModel(modelo);

        // Ajustar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(400);
    }

    private void limpiar() {
        idSeleccionado = null;
        txtNombre.setText("");
        txtDescripcion.setText("");
        tabla.clearSelection();
        txtNombre.requestFocus();
    }


    private boolean validarCampos() {
        // Validar nombre con mínimo 3 caracteres
        if (txtNombre.getText().trim().length() < 3) {
            mostrarMensaje("El nombre debe tener al menos 3 caracteres",
                    "Validación de Datos", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }

        // Validar descripción con mínimo 3 caracteres
        if (txtDescripcion.getText().trim().length() < 3) {
            mostrarMensaje("La descripción debe tener al menos 3 caracteres",
                    "Validación de Datos", JOptionPane.WARNING_MESSAGE);
            txtDescripcion.requestFocus();
            return false;
        }

        return true;
    }

    private void guardar() {
        if (!validarCampos()) {
            return;
        }

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(idSeleccionado != null ? idSeleccionado : 0);
        categoria.setNombre(txtNombre.getText().trim());
        categoria.setDescripcion(txtDescripcion.getText().trim());

        try {
            if (idSeleccionado == null) {
                categoriaDAO.insertar(categoria);
                mostrarMensaje("Categoría guardada exitosamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                categoriaDAO.actualizar(categoria);
                mostrarMensaje("Categoría actualizada exitosamente", "Éxito",
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
            mostrarMensaje("Debe seleccionar una categoría de la tabla", "Selección requerida",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        idSeleccionado = (Integer) tabla.getValueAt(fila, 0);
        txtNombre.setText(String.valueOf(tabla.getValueAt(fila, 1)));
        txtDescripcion.setText(String.valueOf(tabla.getValueAt(fila, 2)));
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Debe seleccionar una categoría de la tabla", "Selección requerida",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer id = (Integer) tabla.getValueAt(fila, 0);
        String nombre = String.valueOf(tabla.getValueAt(fila, 1));

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la categoría '" + nombre + "'?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                categoriaDAO.eliminar(id);
                mostrarMensaje("Categoría eliminada exitosamente", "Éxito",
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
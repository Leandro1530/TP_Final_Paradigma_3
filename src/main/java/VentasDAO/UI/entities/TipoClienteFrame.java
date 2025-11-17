package VentasDAO.UI.entities;

import VentasDAO.DAO.TipoClienteDAO;
import VentasDAO.Objetos.TipoCliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;


public class TipoClienteFrame extends JDialog {

    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();

    private final JTable tabla = new JTable();
    private final JTextField txtNombre = new JTextField(25);
    private final JTextField txtDescripcion = new JTextField(25);
    private Integer idSeleccionado;

    // Colores del tema
    private static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(52, 152, 219);
    private static final Color COLOR_EXITO = new Color(39, 174, 96);
    private static final Color COLOR_PELIGRO = new Color(231, 76, 60);
    private static final Color COLOR_ADVERTENCIA = new Color(243, 156, 18);
    private static final Color COLOR_FONDO = new Color(236, 240, 241);
    private static final Color COLOR_PANEL = Color.WHITE;

    public TipoClienteFrame(java.awt.Window owner) {
        super(owner, "Gestión de Tipos de Cliente", ModalityType.APPLICATION_MODAL);
        setSize(900, 550);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COLOR_FONDO);

        // Panel principal con márgenes
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(COLOR_FONDO);

        // Panel de formulario
        panelPrincipal.add(crearPanelFormulario(), BorderLayout.NORTH);

        // Panel de tabla
        panelPrincipal.add(crearPanelTabla(), BorderLayout.CENTER);

        // Panel de botones
        panelPrincipal.add(crearPanelBotones(), BorderLayout.SOUTH);

        add(panelPrincipal);

        refrescarTabla();
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
                        " Datos del Tipo de Cliente ",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        COLOR_PRIMARIO),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBackground(COLOR_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        contenido.add(crearEtiqueta("Nombre:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        contenido.add(estilizarCampoTexto(txtNombre), gbc);

        // Descripción
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        contenido.add(crearEtiqueta("Descripción:"), gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        contenido.add(estilizarCampoTexto(txtDescripcion), gbc);

        panel.add(contenido, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
                        " Lista de Tipos de Cliente ",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        COLOR_PRIMARIO),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Estilizar tabla
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(30);
        tabla.setSelectionBackground(new Color(52, 152, 219, 100));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setGridColor(new Color(189, 195, 199));
        tabla.setShowVerticalLines(true);
        tabla.setShowHorizontalLines(true);

        // Estilizar encabezado
        JTableHeader header = tabla.getTableHeader();
        header.setBackground(COLOR_PRIMARIO);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(COLOR_FONDO);

        JButton btnGuardar = crearBotonEstilizado(" Guardar", COLOR_EXITO);
        JButton btnEditar = crearBotonEstilizado(" Editar", COLOR_SECUNDARIO);
        JButton btnEliminar = crearBotonEstilizado(" Eliminar", COLOR_PELIGRO);
        JButton btnLimpiar = crearBotonEstilizado(" Limpiar", COLOR_ADVERTENCIA);

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> cargarSeleccion());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        panel.add(btnLimpiar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnGuardar);

        return panel;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(44, 62, 80));
        return label;
    }

    private JTextField estilizarCampoTexto(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return campo;
    }

    private JButton crearBotonEstilizado(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(140, 40));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efectos hover
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

    private void refrescarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Descripción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (TipoCliente tipo : tipoClienteDAO.listar()) {
            modelo.addRow(new Object[]{
                    tipo.getIdTipoCliente(),
                    tipo.getNombre(),
                    tipo.getDescripcion()
            });
        }
        tabla.setModel(modelo);

        // Ajustar ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(400);
    }

    private void limpiar() {
        idSeleccionado = null;
        txtNombre.setText("");
        txtDescripcion.setText("");
        tabla.clearSelection();
    }

    private void guardar() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje("El nombre es obligatorio", "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setIdTipoCliente(idSeleccionado != null ? idSeleccionado : 0);
        tipoCliente.setNombre(txtNombre.getText().trim());
        tipoCliente.setDescripcion(txtDescripcion.getText().trim());

        try {
            if (idSeleccionado == null) {
                tipoClienteDAO.insertar(tipoCliente);
                mostrarMensaje("Tipo de cliente guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                tipoClienteDAO.actualizar(tipoCliente);
                mostrarMensaje("Tipo de cliente actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            limpiar();
            refrescarTabla();
        } catch (SQLException ex) {
            mostrarMensaje("Error en la base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Debe seleccionar un tipo de cliente de la tabla", "Ninguna Selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        idSeleccionado = (Integer) tabla.getValueAt(fila, 0);
        txtNombre.setText(String.valueOf(tabla.getValueAt(fila, 1)));
        txtDescripcion.setText(String.valueOf(tabla.getValueAt(fila, 2)));
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Debe seleccionar un tipo de cliente de la tabla", "Ninguna Selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer id = (Integer) tabla.getValueAt(fila, 0);
        String nombre = String.valueOf(tabla.getValueAt(fila, 1));

        if (id == null) {
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el tipo de cliente '" + nombre + "' (ID: " + id + ")?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                tipoClienteDAO.eliminar(id);
                mostrarMensaje("Tipo de cliente eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiar();
                refrescarTabla();
            } catch (SQLException ex) {
                mostrarMensaje("Error al eliminar: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
}
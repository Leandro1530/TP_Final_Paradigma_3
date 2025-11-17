package VentasDAO.UI.entities;

import VentasDAO.DAO.CategoriaDAO;
import VentasDAO.DAO.ProductoDAO;
import VentasDAO.Objetos.Categoria;
import VentasDAO.Objetos.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;


public class ProductoFrame extends JDialog {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private final JTable tabla = new JTable();
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtDescripcion = new JTextField(20);
    private final JTextField txtPrecio = new JTextField(10);
    private final JTextField txtStock = new JTextField(10);
    private final JComboBox<Categoria> cbCategoria = new JComboBox<>();
    private Integer idSeleccionado;

    // Colores del tema
    private static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(52, 152, 219);
    private static final Color COLOR_EXITO = new Color(39, 174, 96);
    private static final Color COLOR_PELIGRO = new Color(231, 76, 60);
    private static final Color COLOR_ADVERTENCIA = new Color(243, 156, 18);
    private static final Color COLOR_FONDO = new Color(236, 240, 241);
    private static final Color COLOR_PANEL = Color.WHITE;

    public ProductoFrame(java.awt.Window owner) {
        super(owner, "Gestión de Productos", ModalityType.APPLICATION_MODAL);
        setSize(1000, 650);
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

        cargarCategorias();
        refrescarTabla();
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
                        " Datos del Producto ",
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

        // Fila 1: Nombre y Descripción
        gbc.gridx = 0; gbc.gridy = 0;
        contenido.add(crearEtiqueta("Nombre:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        contenido.add(estilizarCampoTexto(txtNombre), gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        contenido.add(crearEtiqueta("Descripción:"), gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        contenido.add(estilizarCampoTexto(txtDescripcion), gbc);

        // Fila 2: Precio, Stock y Categoría
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        contenido.add(crearEtiqueta("Precio:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        contenido.add(estilizarCampoTexto(txtPrecio), gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        contenido.add(crearEtiqueta("Stock:"), gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        contenido.add(estilizarCampoTexto(txtStock), gbc);

        // Fila 3: Categoría (ocupa todo el ancho)
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        contenido.add(crearEtiqueta("Categoría:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        estilizarComboBox(cbCategoria);
        contenido.add(cbCategoria, gbc);

        panel.add(contenido, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
                        " Lista de Productos ",
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

    private void estilizarComboBox(JComboBox<Categoria> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        ((JComponent) combo.getRenderer()).setBorder(new EmptyBorder(5, 8, 5, 8));
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

    private void cargarCategorias() {
        cbCategoria.removeAllItems();
        for (Categoria categoria : categoriaDAO.listar()) {
            cbCategoria.addItem(categoria);
        }
        cbCategoria.setSelectedIndex(-1);
    }

    private void refrescarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Descripción", "Precio", "Stock", "Categoría"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Producto producto : productoDAO.listar()) {
            modelo.addRow(new Object[]{
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    String.format("$%.2f", producto.getPrecio()),
                    producto.getStock(),
                    producto.getCategoria()
            });
        }
        tabla.setModel(modelo);

        // Ajustar ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(250);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(150);
    }

    private void limpiar() {
        idSeleccionado = null;
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        cbCategoria.setSelectedIndex(-1);
        tabla.clearSelection();
    }

    private void guardar() {
        try {
            if (txtNombre.getText().trim().isEmpty()
                    || txtDescripcion.getText().trim().isEmpty()
                    || txtPrecio.getText().trim().isEmpty()
                    || txtStock.getText().trim().isEmpty()) {
                mostrarMensaje("Todos los campos son obligatorios", "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Categoria seleccionada = (Categoria) cbCategoria.getSelectedItem();
            if (seleccionada == null || seleccionada.getIdCategoria() <= 0) {
                mostrarMensaje("Debe seleccionar una categoría", "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            Integer stock = Integer.valueOf(txtStock.getText().trim());

            if (precio.compareTo(BigDecimal.ZERO) < 0) {
                mostrarMensaje("El precio no puede ser negativo", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (stock < 0) {
                mostrarMensaje("El stock no puede ser negativo", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producto producto = new Producto();
            producto.setIdProducto(idSeleccionado != null ? idSeleccionado : 0);
            producto.setNombre(txtNombre.getText().trim());
            producto.setDescripcion(txtDescripcion.getText().trim());
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setCategoria(seleccionada);

            if (idSeleccionado == null) {
                productoDAO.insertar(producto);
                mostrarMensaje("Producto guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                productoDAO.actualizar(producto);
                mostrarMensaje("Producto actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            limpiar();
            refrescarTabla();
        } catch (NumberFormatException ex) {
            mostrarMensaje("Precio o stock inválido. Verifique los valores ingresados", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            mostrarMensaje("Error en la base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Debe seleccionar un producto de la tabla", "Ninguna Selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        idSeleccionado = (Integer) tabla.getValueAt(fila, 0);
        txtNombre.setText(String.valueOf(tabla.getValueAt(fila, 1)));
        txtDescripcion.setText(String.valueOf(tabla.getValueAt(fila, 2)));

        // Remover formato del precio
        String precioStr = String.valueOf(tabla.getValueAt(fila, 3)).replace("$", "").trim();
        txtPrecio.setText(precioStr);
        txtStock.setText(String.valueOf(tabla.getValueAt(fila, 4)));

        Categoria categoria = (Categoria) tabla.getValueAt(fila, 5);
        if (categoria != null) {
            for (int i = 0; i < cbCategoria.getItemCount(); i++) {
                Categoria item = cbCategoria.getItemAt(i);
                if (item != null && item.getIdCategoria() == categoria.getIdCategoria()) {
                    cbCategoria.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            cbCategoria.setSelectedIndex(-1);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Debe seleccionar un producto de la tabla", "Ninguna Selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer id = (Integer) tabla.getValueAt(fila, 0);
        String nombre = String.valueOf(tabla.getValueAt(fila, 1));

        if (id == null) {
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el producto '" + nombre + "' (ID: " + id + ")?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                productoDAO.eliminar(id);
                mostrarMensaje("Producto eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
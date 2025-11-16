package VentasDAO.UI.entities;

import VentasDAO.DAO.CategoriaDAO;
import VentasDAO.DAO.ProductoDAO;
import VentasDAO.Objetos.Categoria;
import VentasDAO.Objetos.Producto;

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
import java.sql.SQLException;

/**
 * ABM básico para {@link Producto}.
 */
public class ProductoFrame extends JDialog {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private final JTable tabla = new JTable();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JTextField txtStock = new JTextField();
    private final JComboBox<Categoria> cbCategoria = new JComboBox<>();
    private Integer idSeleccionado;

    public ProductoFrame(java.awt.Window owner) {
        super(owner, "Productos", ModalityType.APPLICATION_MODAL);
        setSize(900, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridLayout(2, 5, 8, 8));
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Descripción:"));
        panelFormulario.add(txtDescripcion);
        panelFormulario.add(new JLabel("Precio:"));
        panelFormulario.add(txtPrecio);
        panelFormulario.add(new JLabel("Stock:"));
        panelFormulario.add(txtStock);
        panelFormulario.add(new JLabel("Categoría:"));
        panelFormulario.add(cbCategoria);
        add(panelFormulario, BorderLayout.NORTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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

        cargarCategorias();
        refrescarTabla();
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
                new Object[]{"ID", "Nombre", "Descripción", "Precio", "Stock", "Categoría"}, 0);
        for (Producto producto : productoDAO.listar()) {
            modelo.addRow(new Object[]{
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecio(),
                    producto.getStock(),
                    producto.getCategoria()
            });
        }
        tabla.setModel(modelo);
    }

    private void limpiar() {
        idSeleccionado = null;
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        cbCategoria.setSelectedIndex(-1);
    }

    private void guardar() {
        try {
            if (txtNombre.getText().trim().isEmpty()
                    || txtDescripcion.getText().trim().isEmpty()
                    || txtPrecio.getText().trim().isEmpty()
                    || txtStock.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Categoria seleccionada = (Categoria) cbCategoria.getSelectedItem();
            if (seleccionada == null || seleccionada.getIdCategoria() <= 0) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una categoría", "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            Integer stock = Integer.valueOf(txtStock.getText().trim());

            Producto producto = new Producto();
            producto.setIdProducto(idSeleccionado != null ? idSeleccionado : 0);
            producto.setNombre(txtNombre.getText().trim());
            producto.setDescripcion(txtDescripcion.getText().trim());
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setCategoria(seleccionada);

            if (idSeleccionado == null) {
                productoDAO.insertar(producto);
            } else {
                productoDAO.actualizar(producto);
            }
            limpiar();
            refrescarTabla();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio o stock inválido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        idSeleccionado = (Integer) tabla.getValueAt(fila, 0);
        txtNombre.setText(String.valueOf(tabla.getValueAt(fila, 1)));
        txtDescripcion.setText(String.valueOf(tabla.getValueAt(fila, 2)));
        txtPrecio.setText(String.valueOf(tabla.getValueAt(fila, 3)));
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
            return;
        }
        Integer id = (Integer) tabla.getValueAt(fila, 0);
        if (id == null) {
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar producto " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                productoDAO.eliminar(id);
                limpiar();
                refrescarTabla();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
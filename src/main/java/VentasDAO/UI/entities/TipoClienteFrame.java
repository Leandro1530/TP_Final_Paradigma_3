package VentasDAO.UI.entities;

import VentasDAO.DAO.TipoClienteDAO;
import VentasDAO.Objetos.TipoCliente;

import javax.swing.JButton;
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
import java.sql.SQLException;

/**
 * ABM básico para {@link TipoCliente}.
 */
public class TipoClienteFrame extends JDialog {

    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();

    private final JTable tabla = new JTable();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private Integer idSeleccionado;

    public TipoClienteFrame(java.awt.Window owner) {
        super(owner, "Tipos de Cliente", ModalityType.APPLICATION_MODAL);
        setSize(700, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridLayout(1, 4, 8, 8));
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Descripción:"));
        panelFormulario.add(txtDescripcion);
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

        refrescarTabla();
    }

    private void refrescarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Nombre", "Descripción"}, 0);
        for (TipoCliente tipo : tipoClienteDAO.listar()) {
            modelo.addRow(new Object[]{tipo.getIdTipoCliente(), tipo.getNombre(), tipo.getDescripcion()});
        }
        tabla.setModel(modelo);
    }

    private void limpiar() {
        idSeleccionado = null;
        txtNombre.setText("");
        txtDescripcion.setText("");
    }

    private void guardar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setIdTipoCliente(idSeleccionado != null ? idSeleccionado : 0);
        tipoCliente.setNombre(txtNombre.getText().trim());
        tipoCliente.setDescripcion(txtDescripcion.getText().trim());
        try {
            if (idSeleccionado == null) {
                tipoClienteDAO.insertar(tipoCliente);
            } else {
                tipoClienteDAO.actualizar(tipoCliente);
            }
            limpiar();
            refrescarTabla();
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
                "¿Eliminar tipo de cliente " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                tipoClienteDAO.eliminar(id); // Pasa int directamente
                limpiar();
                refrescarTabla();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
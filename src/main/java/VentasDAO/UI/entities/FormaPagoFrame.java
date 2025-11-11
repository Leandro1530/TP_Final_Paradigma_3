package VentasDAO.UI.entities;

import VentasDAO.DAO.FormaPagoDAO;
import VentasDAO.Objetos.FormaPago;

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
 * ABM básico para {@link FormaPago}.
 */
public class FormaPagoFrame extends JDialog {

    private final FormaPagoDAO formaPagoDAO = new FormaPagoDAO();

    private final JTable tabla = new JTable();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private int idSeleccionado = 0; // Cambiado a int primitivo, 0 = no seleccionado

    public FormaPagoFrame(java.awt.Window owner) {
        super(owner, "Formas de Pago", ModalityType.APPLICATION_MODAL);
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
        for (FormaPago formaPago : formaPagoDAO.listar()) {
            modelo.addRow(new Object[]{formaPago.getIdFormaPago(), formaPago.getNombre(), formaPago.getDescripcion()});
        }
        tabla.setModel(modelo);
    }

    private void limpiar() {
        idSeleccionado = 0; // 0 indica que no hay selección
        txtNombre.setText("");
        txtDescripcion.setText("");
    }

    private void guardar() {
        FormaPago formaPago = new FormaPago(idSeleccionado, txtNombre.getText(), txtDescripcion.getText());
        try {
            if (idSeleccionado == 0) { // Si es 0, es inserción
                formaPagoDAO.insertar(formaPago);
            } else {
                formaPagoDAO.actualizar(formaPago);
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
        idSeleccionado = (Integer) tabla.getValueAt(fila, 0); // Cast seguro desde tabla
        txtNombre.setText(String.valueOf(tabla.getValueAt(fila, 1)));
        txtDescripcion.setText(String.valueOf(tabla.getValueAt(fila, 2)));
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        Integer id = (Integer) tabla.getValueAt(fila, 0);
        if (id == null || id == 0) { // Validación adicional
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar forma de pago " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                formaPagoDAO.eliminar(id); // Pasa int directamente
                limpiar();
                refrescarTabla();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
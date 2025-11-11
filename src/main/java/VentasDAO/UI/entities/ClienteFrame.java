package VentasDAO.UI.entities;

import VentasDAO.DAO.ClienteDAO;
import VentasDAO.DAO.TipoClienteDAO;
import VentasDAO.Objetos.Cliente;
import VentasDAO.Objetos.TipoCliente;

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
import java.sql.SQLException;

/**
 * ABM básico para la entidad {@link Cliente}.
 */
public class ClienteFrame extends JDialog {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();

    private final JTable tablaClientes = new JTable();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtApellido = new JTextField();
    private final JTextField txtDni = new JTextField();
    private final JTextField txtDireccion = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JComboBox<TipoCliente> cbTipo = new JComboBox<>();

    private Integer idSeleccionado;

    public ClienteFrame(java.awt.Window owner) {
        super(owner, "Clientes", ModalityType.APPLICATION_MODAL);
        setSize(900, 560);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridLayout(3, 4, 8, 8));
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Apellido:"));
        panelFormulario.add(txtApellido);
        panelFormulario.add(new JLabel("DNI:"));
        panelFormulario.add(txtDni);
        panelFormulario.add(new JLabel("Dirección:"));
        panelFormulario.add(txtDireccion);
        panelFormulario.add(new JLabel("Teléfono:"));
        panelFormulario.add(txtTelefono);
        panelFormulario.add(new JLabel("Email:"));
        panelFormulario.add(txtEmail);
        panelFormulario.add(new JLabel("Tipo Cliente:"));
        panelFormulario.add(cbTipo);
        add(panelFormulario, BorderLayout.NORTH);

        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

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

        cargarTipos();
        refrescarTabla();
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
                new Object[]{"ID", "Nombre", "Apellido", "DNI", "Dirección", "Teléfono", "Email", "Tipo"}, 0);

        for (Cliente cliente : clienteDAO.listar()) {
            modelo.addRow(new Object[]{
                    cliente.getIdCliente(),
                    cliente.getNombre(),
                    cliente.getApellido(),
                    cliente.getDni(),
                    cliente.getDireccion(),
                    cliente.getTelefono(),
                    cliente.getEmail(),
                    cliente.getIdTipoCliente()
            });
        }
        tablaClientes.setModel(modelo);
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
    }

    private void guardar() {
        TipoCliente seleccionado = (TipoCliente) cbTipo.getSelectedItem();
        Integer idTipo = seleccionado != null ? seleccionado.getIdTipoCliente() : null;

        Cliente cliente = new Cliente(
                idSeleccionado,
                txtNombre.getText(),
                txtApellido.getText(),
                txtDni.getText(),
                txtDireccion.getText(),
                txtTelefono.getText(),
                txtEmail.getText(),
                idTipo);
        try {
            if (idSeleccionado == null) {
                clienteDAO.insertar(cliente);
            } else {
                clienteDAO.actualizar(cliente);
            }
            limpiar();
            refrescarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarSeleccion() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            return;
        }
        idSeleccionado = (Integer) tablaClientes.getValueAt(fila, 0);
        txtNombre.setText(String.valueOf(tablaClientes.getValueAt(fila, 1)));
        txtApellido.setText(String.valueOf(tablaClientes.getValueAt(fila, 2)));
        txtDni.setText(String.valueOf(tablaClientes.getValueAt(fila, 3)));
        txtDireccion.setText(String.valueOf(tablaClientes.getValueAt(fila, 4)));
        txtTelefono.setText(String.valueOf(tablaClientes.getValueAt(fila, 5)));
        txtEmail.setText(String.valueOf(tablaClientes.getValueAt(fila, 6)));

        Integer idTipo = (Integer) tablaClientes.getValueAt(fila, 7);
        if (idTipo != null) {
            for (int i = 0; i < cbTipo.getItemCount(); i++) {
                TipoCliente item = cbTipo.getItemAt(i);
                if (item != null && idTipo.equals(item.getIdTipoCliente())) {
                    cbTipo.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            cbTipo.setSelectedIndex(-1);
        }
    }

    private void eliminar() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            return;
        }
        Integer id = (Integer) tablaClientes.getValueAt(fila, 0);
        if (id == null) {
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar cliente " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                clienteDAO.eliminar(id);
                limpiar();
                refrescarTabla();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

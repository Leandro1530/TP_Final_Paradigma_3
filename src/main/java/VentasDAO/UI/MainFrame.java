package VentasDAO.UI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import VentasDAO.UI.entities.*;
/**
 * Ventana principal simplificada para navegar por los ABM.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Sistema de Ventas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JToolBar toolbar = new JToolBar();
        JButton btnClientes = new JButton("Clientes");
        JButton btnProductos = new JButton("Productos");
        JButton btnCategorias = new JButton("Categorías");
        JButton btnTipos = new JButton("Tipos");
        JButton btnPagos = new JButton("Formas de pago");
        JButton btnFacturas = new JButton("Facturas");

        toolbar.add(btnClientes);
        toolbar.add(btnProductos);
        toolbar.add(btnCategorias);
        toolbar.add(btnTipos);
        toolbar.add(btnPagos);
        toolbar.addSeparator();
        toolbar.add(btnFacturas);
        add(toolbar, BorderLayout.NORTH);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 80));
        JLabel lbl = new JLabel("Seleccione una opción en la barra superior para comenzar.");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(lbl);
        add(center, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemScript = new JMenuItem("Script SQL requerido");
        itemScript.addActionListener(e -> SwingUtilities.invokeLater(() ->
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Ejecutá el script database/scriptDB_nanotecch.sql en tu gestor SQL antes de usar el sistema.",
                        "Información",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE)));
        menuAyuda.add(itemScript);
        menuBar.add(menuAyuda);
        setJMenuBar(menuBar);

        btnClientes.addActionListener(e -> new ClienteFrame(this).setVisible(true));
        btnProductos.addActionListener(e -> new ProductoFrame(this).setVisible(true));
        btnCategorias.addActionListener(e -> new CategoriaFrame(this).setVisible(true));
        btnTipos.addActionListener(e -> new TipoClienteFrame(this).setVisible(true));
        btnPagos.addActionListener(e -> new FormaPagoFrame(this).setVisible(true));
        btnFacturas.addActionListener(e -> new FacturaFrame(this).setVisible(true));
    }
}

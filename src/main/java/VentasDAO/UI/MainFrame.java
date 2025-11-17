package VentasDAO.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import VentasDAO.UI.entities.*;


public class MainFrame extends JFrame {

    // Colores del tema
    private static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(52, 152, 219);
    private static final Color COLOR_OSCURO = new Color(44, 62, 80);
    private static final Color COLOR_FONDO = new Color(236, 240, 241);
    private static final Color COLOR_PANEL = Color.WHITE;
    private static final Color COLOR_ACENTO = new Color(39, 174, 96);

    public MainFrame() {
        setTitle("Sistema de Gestión de Ventas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);

        // Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Panel superior con toolbar
        add(crearPanelSuperior(), BorderLayout.NORTH);

        // Panel central con dashboard
        add(crearPanelCentral(), BorderLayout.CENTER);

        // Panel inferior con información
        add(crearPanelInferior(), BorderLayout.SOUTH);

    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRIMARIO);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Logo y título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelTitulo.setBackground(COLOR_PRIMARIO);

        JLabel lblIcono = new JLabel("🏪");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JLabel lblTitulo = new JLabel("Sistema de Gestión de Ventas NANOTECH");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);

        panelTitulo.add(lblIcono);
        panelTitulo.add(lblTitulo);

        panel.add(panelTitulo, BorderLayout.WEST);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de bienvenida
        JPanel panelBienvenida = new JPanel(new BorderLayout());
        panelBienvenida.setBackground(COLOR_PANEL);
        panelBienvenida.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_SECUNDARIO, 2),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblBienvenida = new JLabel("<html><center>¡Bienvenido al Sistema de Gestión de Ventas NANOTECH!<br><br>" );
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lblBienvenida.setForeground(COLOR_OSCURO);
        lblBienvenida.setHorizontalAlignment(SwingConstants.CENTER);
        panelBienvenida.add(lblBienvenida, BorderLayout.CENTER);

        panel.add(panelBienvenida, BorderLayout.NORTH);

        // Panel de módulos
        JPanel panelModulos = new JPanel(new GridLayout(2, 4, 15, 15));
        panelModulos.setBackground(COLOR_FONDO);

        // Crear tarjetas de módulos
        panelModulos.add(crearTarjetaModulo("👥", "Clientes", "Gestión de clientes",
                e -> new ClienteFrame(this).setVisible(true)));

        panelModulos.add(crearTarjetaModulo("📦", "Productos", "Catálogo de productos",
                e -> new ProductoFrame(this).setVisible(true)));

        panelModulos.add(crearTarjetaModulo("🏷️", "Categorías", "Categorías de productos",
                e -> new CategoriaFrame(this).setVisible(true)));

        panelModulos.add(crearTarjetaModulo("⭐", "Tipos de Cliente", "Clasificación de clientes",
                e -> new TipoClienteFrame(this).setVisible(true)));

        panelModulos.add(crearTarjetaModulo("💳", "Formas de Pago", "Métodos de pago",
                e -> new FormaPagoFrame(this).setVisible(true)));

        panelModulos.add(crearTarjetaModulo("📄", "Detalles", "Detalles de facturas",
                e -> new DetalleFacturaFrame(this).setVisible(true)));

        panelModulos.add(crearTarjetaModulo("🧾", "Facturas", "Gestión de facturas",
                e -> new FacturaFrame(this).setVisible(true)));

        panel.add(panelModulos, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearTarjetaModulo(String icono, String titulo, String descripcion,
                                      java.awt.event.ActionListener action) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(COLOR_PANEL);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(20, 15, 20, 15)
        ));
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icono
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Título
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Descripción
        JLabel lblDescripcion = new JLabel(descripcion);
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDescripcion.setForeground(new Color(127, 140, 141));
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);

        tarjeta.add(lblIcono);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 10)));
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 5)));
        tarjeta.add(lblDescripcion);

        // Efectos hover
        tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tarjeta.setBackground(new Color(236, 240, 241));
                tarjeta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_SECUNDARIO, 2),
                        new EmptyBorder(19, 14, 19, 14)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tarjeta.setBackground(COLOR_PANEL);
                tarjeta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                        new EmptyBorder(20, 15, 20, 15)
                ));
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                action.actionPerformed(null);
            }
        });

        return tarjeta;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_OSCURO);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblCopyright = new JLabel("© 2025 Sistema de Gestión de Ventas - Todos los derechos reservados");
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCopyright.setForeground(Color.WHITE);

        JLabel lblVersion = new JLabel("Versión 1.0.0");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVersion.setForeground(new Color(189, 195, 199));

        panel.add(lblCopyright, BorderLayout.WEST);
        panel.add(lblVersion, BorderLayout.EAST);

        return panel;
    }

}
package com.yovani.ventas.ui;

import com.yovani.ventas.db.DbInstaller;
import com.yovani.ventas.ui.entities.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Sistema de Ventas (v2)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JToolBar bar = new JToolBar();
        JButton bClientes=new JButton("Clientes");
        JButton bProductos=new JButton("Productos");
        JButton bCategorias=new JButton("Categorías");
        JButton bTipos=new JButton("Tipos de Cliente");
        JButton bPagos=new JButton("Formas de Pago");
        JButton bFactura=new JButton("Factura");
        bar.add(bClientes); bar.add(bProductos); bar.add(bCategorias); bar.add(bTipos); bar.add(bPagos); bar.addSeparator(); bar.add(bFactura);
        add(bar, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.add(new JLabel("Bienvenido. Use la barra superior para navegar."));
        add(center, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu herramientas = new JMenu("Herramientas");
        JMenuItem initDb = new JMenuItem("Inicializar Base (ejecutar Nueva.sql)");
        initDb.addActionListener(e -> DbInstaller.runSqlFile(this));
        herramientas.add(initDb);
        menuBar.add(herramientas);
        setJMenuBar(menuBar);

        bClientes.addActionListener(e -> new ClienteFrame(this).setVisible(true));
        bProductos.addActionListener(e -> new ProductoFrame(this).setVisible(true));
        bCategorias.addActionListener(e -> new CategoriaFrame(this).setVisible(true));
        bTipos.addActionListener(e -> new TipoClienteFrame(this).setVisible(true));
        bPagos.addActionListener(e -> new FormaPagoFrame(this).setVisible(true));
        bFactura.addActionListener(e -> new FacturaFrame(this).setVisible(true));
    }
}

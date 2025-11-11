package VentasDAO;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.yovani.ventas.ui.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatIntelliJLaf()); } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
package VentasDAO;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import VentasDAO.UI.MainFrame;  

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error al aplicar tema: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrame().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error al inicializar MainFrame: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}

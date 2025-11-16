package VentasDAO;

import java.awt.EventQueue;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import VentasDAO.UI.MainFrame;

/**
 * Punto de entrada del sistema. Centraliza la configuración de la apariencia y
 * garantiza que la interfaz se inicialice dentro del EDT.
 */
public final class Main {

    private Main() {
        throw new IllegalStateException("Utility class");
    }

    public static void main(String[] args) {
        configurarLookAndFeel();
        registrarManejadorGlobal();

        EventQueue.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                mostrarErrorInicio(e);
            }
        });
    }

    private static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el tema del sistema: " + e.getMessage());
        }
    }

    private static void registrarManejadorGlobal() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Error no controlado en " + thread.getName());
            throwable.printStackTrace(System.err);
            SwingUtilities.invokeLater(() -> mostrarErrorInicio(throwable));
        });
    }

    private static void mostrarErrorInicio(Throwable throwable) {
        JOptionPane.showMessageDialog(
                null,
                "No se pudo iniciar la aplicación. Revisá la consola para más detalles.\n" + throwable.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

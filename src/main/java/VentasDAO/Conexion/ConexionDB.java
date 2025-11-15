package VentasDAO.Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexión JDBC a PostgreSQL usando patrón Singleton.
 */
public class ConexionDB {

    // Datos de conexión
    private static final String URL = "jdbc:postgresql://localhost:5433/Ventas";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Nano-987";
    private static final String DRIVER = "org.postgresql.Driver";

    // Única instancia de Connection
    private static Connection connection = null;

    // Constructor privado (Singleton)
    private ConexionDB() {
    }

    /**
     * Devuelve la conexión activa. Si no existe o está cerrada, la crea.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión establecida a PostgreSQL (" + URL + ")");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver PostgreSQL no encontrado.");
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.err.println("Error: No se pudo conectar a la base de datos.");
            throw new RuntimeException(e);
        }
        return connection;
    }

    /**
     * Cierra la conexión y limpia la instancia.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Indica si la conexión está activa.
     */
    public static boolean isConnectionActive() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Devuelve información básica del estado de la conexión.
     */
    public static String getConnectionInfo() {
        try {
            if (connection != null && !connection.isClosed()) {
                return "Conexión ACTIVA a " + connection.getCatalog() +
                        " (" + connection.getMetaData().getURL() + ")";
            } else {
                return "Conexión CERRADA o NO ESTABLECIDA";
            }
        } catch (SQLException e) {
            return "Error al obtener información: " + e.getMessage();
        }
    }

    /**
     * Main de prueba rápida de conexión.
     */
    public static void main(String[] args) {
        System.out.println("Probando conexión a la base de datos...");
        try {
            Connection conn = ConexionDB.getConnection();
            System.out.println("OK: " + getConnectionInfo());
        } catch (Exception e) {
            System.err.println("Fallo en la prueba de conexión:");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
}

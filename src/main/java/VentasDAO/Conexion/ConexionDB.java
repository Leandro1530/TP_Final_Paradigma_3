package VentasDAO.Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para gestionar conexiones a PostgreSQL usando JDBC nativo
 * Implementa un pool simple manual para optimizar rendimiento
 *
 * @author Leanro E. Acosta Garcia
 * Paradigmas y Lenguajes III - TP Final
 * Universidad Nacional de La Rioja
 */
public class ConexionDB {

    // ========== CONFIGURACIÓN DE LA BASE DE DATOS ==========
    private static final String URL = "jdbc:postgresql://localhost:5433/Ventas";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Nano-987";
    private static final String DRIVER = "org.postgresql.Driver";

    // ========== POOL SIMPLE DE CONEXIONES ==========
    private static final int POOL_SIZE = 5;  // Máximo de conexiones simultáneas
    private static final List<Connection> poolConexiones = new ArrayList<>();
    private static final List<Boolean> conexionesDisponibles = new ArrayList<>();

    // Inicialización del pool al cargar la clase
    static {
        try {
            inicializarPool();
            System.out.println("✓ Pool de conexiones inicializado correctamente");
            System.out.println("  - Base de datos: Ventas");
            System.out.println("  - Servidor: localhost:5432");
            System.out.println("  - Conexiones disponibles: " + POOL_SIZE);
        } catch (Exception e) {
            System.err.println("✗ ERROR CRÍTICO: No se pudo inicializar el pool");
            e.printStackTrace();
        }
    }

    /**
     * Inicializa el pool de conexiones
     * Crea POOL_SIZE conexiones al iniciar la aplicación
     */
    private static void inicializarPool() throws SQLException, ClassNotFoundException {
        // Cargar el driver de PostgreSQL
        Class.forName(DRIVER);

        // Crear las conexiones del pool
        for (int i = 0; i < POOL_SIZE; i++) {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false); // Importante para transacciones manuales
            poolConexiones.add(conn);
            conexionesDisponibles.add(true); // Marcar como disponible
        }
    }

    /**
     * Obtiene una conexión disponible del pool
     * Si no hay disponibles, espera o crea una temporal
     *
     * @return Connection lista para usar
     * @throws SQLException si hay error al conectar
     */
    public static synchronized Connection getConnection() throws SQLException {
        // Buscar una conexión disponible en el pool
        for (int i = 0; i < poolConexiones.size(); i++) {
            if (conexionesDisponibles.get(i)) {
                Connection conn = poolConexiones.get(i);

                // Verificar si la conexión sigue válida
                if (conn.isClosed() || !conn.isValid(2)) {
                    // Reconectar si está cerrada o inválida
                    try {
                        conn = DriverManager.getConnection(URL, USER, PASSWORD);
                        conn.setAutoCommit(false);
                        poolConexiones.set(i, conn);
                    } catch (SQLException e) {
                        System.err.println("✗ Error al reconectar. Índice: " + i);
                        throw e;
                    }
                }

                conexionesDisponibles.set(i, false); // Marcar como en uso
                System.out.println("✓ Conexión obtenida del pool [" + i + "]");
                return conn;
            }
        }

        // Si no hay conexiones disponibles, crear una temporal
        System.out.println("⚠ Pool lleno, creando conexión temporal");
        Connection tempConn = DriverManager.getConnection(URL, USER, PASSWORD);
        tempConn.setAutoCommit(false);
        return tempConn;
    }

    /**
     * Devuelve una conexión al pool (marcándola como disponible)
     *
     * @param conn Conexión a liberar
     */
    public static synchronized void releaseConnection(Connection conn) {
        if (conn == null) return;

        try {
            // Buscar la conexión en el pool
            for (int i = 0; i < poolConexiones.size(); i++) {
                if (poolConexiones.get(i) == conn) {
                    // Limpiar cualquier transacción pendiente
                    if (!conn.getAutoCommit()) {
                        conn.rollback(); // Por seguridad, revertir transacciones no confirmadas
                    }

                    conexionesDisponibles.set(i, true); // Marcar como disponible
                    System.out.println("✓ Conexión devuelta al pool [" + i + "]");
                    return;
                }
            }

            // Si es una conexión temporal (no está en el pool), cerrarla
            if (!conn.isClosed()) {
                conn.close();
                System.out.println("✓ Conexión temporal cerrada");
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al liberar conexión");
            e.printStackTrace();
        }
    }

    /**
     * Cierra una conexión (alias de releaseConnection)
     * Mantiene compatibilidad con código existente
     *
     * @param conn Conexión a cerrar
     */
    public static void closeConnection(Connection conn) {
        releaseConnection(conn);
    }

    /**
     * Confirma (commit) una transacción
     *
     * @param conn Conexión con la transacción
     * @return true si se confirmó exitosamente
     */
    public static boolean commit(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
                System.out.println("✓ Transacción confirmada (COMMIT)");
                return true;
            } catch (SQLException e) {
                System.err.println("✗ Error al hacer COMMIT");
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * Revierte (rollback) una transacción
     *
     * @param conn Conexión con la transacción
     * @return true si se revirtió exitosamente
     */
    public static boolean rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                System.out.println("⚠ Transacción revertida (ROLLBACK)");
                return true;
            } catch (SQLException e) {
                System.err.println("✗ Error al hacer ROLLBACK");
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * Cierra todas las conexiones del pool
     * IMPORTANTE: Llamar al cerrar la aplicación
     */
    public static synchronized void cerrarPool() {
        System.out.println("Cerrando pool de conexiones...");

        for (int i = 0; i < poolConexiones.size(); i++) {
            try {
                Connection conn = poolConexiones.get(i);
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("  ✓ Conexión [" + i + "] cerrada");
                }
            } catch (SQLException e) {
                System.err.println("  ✗ Error al cerrar conexión [" + i + "]");
                e.printStackTrace();
            }
        }

        poolConexiones.clear();
        conexionesDisponibles.clear();
        System.out.println("✓ Pool de conexiones cerrado completamente");
    }

    /**
     * Prueba la conexión a la base de datos
     * Útil para verificar que todo está configurado correctamente
     *
     * @return true si la conexión es exitosa
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            boolean valida = conn.isValid(5); // Timeout de 5 segundos

            if (valida) {
                System.out.println("✓ Test de conexión EXITOSO");
            } else {
                System.out.println("✗ Test de conexión FALLIDO");
            }

            return valida;

        } catch (SQLException e) {
            System.err.println("✗ Test de conexión FALLIDO con excepción");
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                releaseConnection(conn);
            }
        }
    }

    /**
     * Obtiene estadísticas del pool
     * Útil para debugging y monitoreo
     *
     * @return String con el estado del pool
     */
    public static String getEstadisticasPool() {
        int disponibles = 0;
        int enUso = 0;

        for (Boolean disponible : conexionesDisponibles) {
            if (disponible) {
                disponibles++;
            } else {
                enUso++;
            }
        }

        return String.format(
                "Pool: Total=%d | Disponibles=%d | En uso=%d",
                POOL_SIZE, disponibles, enUso
        );
    }

    /**
     * Muestra información detallada del pool
     * Para debugging
     */
    public static void mostrarEstadoPool() {
        System.out.println("\n=== ESTADO DEL POOL DE CONEXIONES ===");
        System.out.println("Tamaño total del pool: " + POOL_SIZE);

        int disponibles = 0;
        int enUso = 0;

        for (int i = 0; i < conexionesDisponibles.size(); i++) {
            String estado = conexionesDisponibles.get(i) ? "DISPONIBLE" : "EN USO";
            System.out.println("  Conexión [" + i + "]: " + estado);

            if (conexionesDisponibles.get(i)) {
                disponibles++;
            } else {
                enUso++;
            }
        }

        System.out.println("\nResumen:");
        System.out.println("  - Disponibles: " + disponibles);
        System.out.println("  - En uso: " + enUso);
        System.out.println("=====================================\n");
    }
}
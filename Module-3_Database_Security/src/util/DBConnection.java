package campusvoice.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Thread-Safe Singleton JDBC Connection Provider for MySQL
 * Reads configuration from db.properties or environment variables.
 * Designed for Member 1 & 3 backend responsibilities (Weeks 4-5).
 */
public class DBConnection {

    private static volatile DBConnection instance;
    private static Properties properties = new Properties();

    private String url;
    private String username;
    private String password;
    private String driver;

    static {
        loadProperties();
    }

    private DBConnection() {
        try {
            driver = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            url = properties.getProperty("db.url", "jdbc:mysql://localhost:3306/campusvoice?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");
            username = properties.getProperty("db.username", "root");
            password = properties.getProperty("db.password", "root");

            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    private static void loadProperties() {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                properties.load(in);
            } else {
                System.out.println("[DBConnection] db.properties not found on classpath, using defaults.");
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Failed to load db.properties: " + e.getMessage());
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Obtains a new JDBC connection from the database.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Tests if the database connection is currently active.
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Safely closes JDBC resources without throwing checked exceptions.
     */
    public static void closeQuietly(AutoCloseable... resources) {
        if (resources == null) return;
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static Properties getProperties() {
        return properties;
    }
}

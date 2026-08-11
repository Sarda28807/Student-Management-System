package com.novastudent.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages database connections for the NovaStudent application.
 * Uses a singleton pattern with configuration file support.
 *
 * Configuration is loaded from config.properties in the project root,
 * or can be overridden via environment variables:
 * - NOVA_DB_URL
 * - NOVA_DB_USERNAME
 * - NOVA_DB_PASSWORD
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private Connection connection;

    private static final String CONFIG_FILE = "config.properties";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/nova_student_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "";

    /**
     * Private constructor — loads configuration.
     */
    private DatabaseConnection() {
        loadConfiguration();
    }

    /**
     * Returns the singleton instance.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Loads database configuration from environment variables or config file.
     * Priority: Environment variables > config.properties > defaults
     */
    private void loadConfiguration() {
        // 1. Try environment variables first
        String envUrl = System.getenv("NOVA_DB_URL");
        String envUser = System.getenv("NOVA_DB_USERNAME");
        String envPass = System.getenv("NOVA_DB_PASSWORD");

        if (envUrl != null && !envUrl.isEmpty()) {
            this.dbUrl = envUrl;
            this.dbUsername = envUser != null ? envUser : DEFAULT_USERNAME;
            this.dbPassword = envPass != null ? envPass : DEFAULT_PASSWORD;
            return;
        }

        // 2. Try config.properties
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
            this.dbUrl = props.getProperty("db.url", DEFAULT_URL);
            this.dbUsername = props.getProperty("db.username", DEFAULT_USERNAME);
            this.dbPassword = props.getProperty("db.password", DEFAULT_PASSWORD);
            return;
        } catch (IOException e) {
            // Config file not found, try classpath
        }

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
                this.dbUrl = props.getProperty("db.url", DEFAULT_URL);
                this.dbUsername = props.getProperty("db.username", DEFAULT_USERNAME);
                this.dbPassword = props.getProperty("db.password", DEFAULT_PASSWORD);
                return;
            }
        } catch (IOException e) {
            // Classpath config not found
        }

        // 3. Use defaults
        this.dbUrl = DEFAULT_URL;
        this.dbUsername = DEFAULT_USERNAME;
        this.dbPassword = DEFAULT_PASSWORD;
    }

    /**
     * Gets an active database connection.
     * Creates a new one if the existing connection is closed or null.
     *
     * @return Active JDBC Connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Ensure mysql-connector-java is in the classpath.", e);
            }
        }
        return connection;
    }

    /**
     * Closes the current connection if open.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * Tests if the database connection is available.
     *
     * @return true if connection is successful
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Returns the database URL (for settings display).
     */
    public String getDbUrl() { return dbUrl; }

    /**
     * Returns the database username (for settings display).
     */
    public String getDbUsername() { return dbUsername; }

    /**
     * Resets the singleton (for testing/reconnection).
     */
    public static synchronized void reset() {
        if (instance != null) {
            instance.closeConnection();
            instance = null;
        }
    }
}

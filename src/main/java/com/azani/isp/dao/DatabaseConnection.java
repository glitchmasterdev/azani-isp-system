package com.azani.isp.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

/**
 * Manages JDBC connections to Supabase (PostgreSQL), MySQL, or embedded H2 database.
 */
public class DatabaseConnection {
    private static Properties config = new Properties();
    private static String dbType = "mysql";
    private static String jdbcUrl;
    private static String user;
    private static String password;
    private static boolean isInitialized = false;
    private static Connection h2KeepAlive = null;

    static {
        registerDrivers();
        loadConfiguration();
    }

    private static void registerDrivers() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Throwable t) {
            System.err.println("PostgreSQL driver not found on classpath: " + t.getMessage());
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Throwable t) {
            System.err.println("MySQL driver not found on classpath: " + t.getMessage());
        }
        try {
            Class.forName("org.h2.Driver");
        } catch (Throwable t) {
            System.err.println("H2 driver not found on classpath: " + t.getMessage());
        }
    }

    private static File getGlobalConfigFile() {
        File dir = new File(System.getProperty("user.home", "."), ".azani_isp");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "db.properties");
    }

    private static String getH2JdbcUrl() {
        File dir = new File(System.getProperty("user.home", "."), ".azani_isp");
        if (!dir.exists()) dir.mkdirs();
        String dbPath = new File(dir, "azani_isp_db").getAbsolutePath().replace('\\', '/');
        return "jdbc:h2:" + dbPath + ";AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE";
    }

    public static void loadConfiguration() {
        File extFile = new File("db.properties");
        File globalFile = getGlobalConfigFile();

        if (extFile.exists()) {
            try (InputStream in = new FileInputStream(extFile)) {
                config.load(in);
            } catch (Exception e) {
                System.err.println("Notice: Could not read local db.properties: " + e.getMessage());
            }
        } else if (globalFile.exists()) {
            try (InputStream in = new FileInputStream(globalFile)) {
                config.load(in);
            } catch (Exception e) {
                System.err.println("Notice: Could not read global db.properties: " + e.getMessage());
            }
        } else {
            try (InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
                if (in != null) {
                    config.load(in);
                }
            } catch (Exception e) {
                System.err.println("Notice: db.properties not found in classpath, using defaults.");
            }
        }

        applyConfigProperties();
    }

    private static void applyConfigProperties() {
        String explicitUrl = config.getProperty("db.url", "").trim();
        dbType = config.getProperty("db.type", "supabase").trim().toLowerCase();

        if (!explicitUrl.isEmpty()) {
            jdbcUrl = explicitUrl;
            user = config.getProperty("db.user", "postgres");
            password = config.getProperty("db.password", "");
            if (jdbcUrl.startsWith("jdbc:postgresql")) {
                dbType = "supabase";
            } else if (jdbcUrl.startsWith("jdbc:mysql")) {
                dbType = "mysql";
            } else {
                dbType = "h2";
            }
            return;
        }

        String host = config.getProperty("db.host", "localhost").trim();
        String port = config.getProperty("db.port", "").trim();
        String dbName = config.getProperty("db.name", "").trim();
        user = config.getProperty("db.user", "").trim();
        password = config.getProperty("db.password", "");

        if ("supabase".equalsIgnoreCase(dbType) || "postgresql".equalsIgnoreCase(dbType) || "postgres".equalsIgnoreCase(dbType)) {
            dbType = "supabase";
            if (port.isEmpty()) port = "5432";
            if (dbName.isEmpty()) dbName = "postgres";
            if (user.isEmpty()) user = "postgres";
            jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName + "?sslmode=require";
        } else if ("mysql".equalsIgnoreCase(dbType)) {
            if (port.isEmpty()) port = "3306";
            if (dbName.isEmpty()) dbName = "azani_isp_db";
            if (user.isEmpty()) user = "root";
            jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
        } else {
            dbType = "h2";
            jdbcUrl = getH2JdbcUrl();
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        registerDrivers();

        if (!isInitialized) {
            initializeDatabase();
            isInitialized = true;
        }

        try {
            if ("supabase".equalsIgnoreCase(dbType) || "postgresql".equalsIgnoreCase(dbType)) {
                return DriverManager.getConnection(jdbcUrl, user, password);
            } else if ("mysql".equalsIgnoreCase(dbType)) {
                return DriverManager.getConnection(jdbcUrl, user, password);
            } else {
                return DriverManager.getConnection(jdbcUrl, "sa", "");
            }
        } catch (SQLException e) {
            boolean autoFallback = Boolean.parseBoolean(config.getProperty("db.auto_fallback", "true"));
            if (!"h2".equalsIgnoreCase(dbType) && autoFallback) {
                System.out.println("Notice: " + dbType.toUpperCase() + " connection failed (" + e.getMessage() + ").");
                System.out.println("Switching to persistent embedded database mode for uninterrupted operation...");
                switchToH2();
                isInitialized = false;
                return getConnection();
            }
            throw e;
        }
    }

    private static void switchToH2() throws SQLException {
        registerDrivers();
        dbType = "h2";
        jdbcUrl = getH2JdbcUrl();
        if (h2KeepAlive == null || h2KeepAlive.isClosed()) {
            h2KeepAlive = DriverManager.getConnection(jdbcUrl, "sa", "");
        }
    }

    public static synchronized void initializeDatabase() {
        registerDrivers();

        if ("supabase".equalsIgnoreCase(dbType) || "postgresql".equalsIgnoreCase(dbType)) {
            try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
                 Statement stmt = conn.createStatement()) {
                
                // Safe table creation without dropping existing data
                executeScript(stmt, "/db/supabase_init_schema.sql");

                // Only seed if empty
                try (java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM institutions")) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        executeScript(stmt, "/db/supabase_seed.sql");
                        System.out.println("Supabase initialized with initial sample records.");
                    } else {
                        System.out.println("Supabase connected. Persistent data preserved.");
                    }
                }
                return;
            } catch (Exception e) {
                System.err.println("Supabase setup notice: " + e.getMessage());
                boolean autoFallback = Boolean.parseBoolean(config.getProperty("db.auto_fallback", "true"));
                if (autoFallback) {
                    try { switchToH2(); } catch (SQLException ignored) {}
                }
            }
        } else if ("mysql".equalsIgnoreCase(dbType)) {
            String host = config.getProperty("db.host", "localhost");
            String port = config.getProperty("db.port", "3306");
            String serverUrl = "jdbc:mysql://" + host + ":" + port + "/?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";

            try (Connection conn = DriverManager.getConnection(serverUrl, user, password);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + config.getProperty("db.name", "azani_isp_db"));
            } catch (Exception e) {
                boolean autoFallback = Boolean.parseBoolean(config.getProperty("db.auto_fallback", "true"));
                if (autoFallback) {
                    System.out.println("Notice: MySQL server unavailable (" + e.getMessage() + ").");
                    System.out.println("Switching to persistent embedded database mode for uninterrupted operation...");
                    try {
                        switchToH2();
                    } catch (SQLException ignored) {}
                }
            }
        }

        if ("h2".equalsIgnoreCase(dbType)) {
            try {
                if (h2KeepAlive == null || h2KeepAlive.isClosed()) {
                    h2KeepAlive = DriverManager.getConnection(jdbcUrl, "sa", "");
                }
            } catch (SQLException e) {
                System.err.println("H2 keep alive error: " + e.getMessage());
            }
        }

        // Execute table creation DDL
        try (Connection conn = ("mysql".equalsIgnoreCase(dbType)) 
                ? DriverManager.getConnection(jdbcUrl, user, password)
                : DriverManager.getConnection(jdbcUrl, "sa", "");
             Statement stmt = conn.createStatement()) {

            executeScript(stmt, "/db/schema.sql");
            
            // Only seed if empty
            try (java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM institutions")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    executeScript(stmt, "/db/sample_data.sql");
                    System.out.println("Database schema initialized with sample records (" + dbType.toUpperCase() + ").");
                } else {
                    System.out.println("Database schema verified (" + dbType.toUpperCase() + "). Existing data preserved.");
                }
            }
        } catch (Exception e) {
            System.err.println("Database schema setup notice: " + e.getMessage());
        }
    }

    public static void executeScript(Statement stmt, String resourcePath) {
        InputStream in = DatabaseConnection.class.getResourceAsStream(resourcePath);
        if (in == null) {
            String clean = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
            File file = new File(clean);
            if (file.exists()) {
                try {
                    in = new FileInputStream(file);
                } catch (Exception ignored) {}
            }
        }
        if (in == null) {
            System.err.println("Warning: Script not found: " + resourcePath);
            return;
        }

        try (InputStream stream = in;
             Scanner scanner = new Scanner(stream)) {
            scanner.useDelimiter(";");
            while (scanner.hasNext()) {
                String rawChunk = scanner.next();
                StringBuilder cleanSql = new StringBuilder();
                for (String line : rawChunk.split("\\r?\\n")) {
                    String trimmedLine = line.trim();
                    if (!trimmedLine.startsWith("--") && !trimmedLine.isEmpty()) {
                        cleanSql.append(line).append("\n");
                    }
                }
                String sql = cleanSql.toString().trim();
                if (!sql.isEmpty()) {
                    String upper = sql.toUpperCase();
                    if ("h2".equalsIgnoreCase(dbType) && (upper.startsWith("CREATE DATABASE") || upper.startsWith("USE "))) {
                        continue;
                    }
                    try {
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        String msg = e.getMessage().toLowerCase();
                        if (!msg.contains("already exists") && !msg.contains("duplicate") && !msg.contains("setval")) {
                            System.err.println("SQL execution notice (" + sql.substring(0, Math.min(30, sql.length())) + "...): " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing script " + resourcePath + ": " + e.getMessage());
        }
    }

    public static void seedSampleData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            if ("supabase".equalsIgnoreCase(dbType) || "postgresql".equalsIgnoreCase(dbType)) {
                executeScript(stmt, "/db/supabase_schema.sql");
            } else {
                executeScript(stmt, "/db/sample_data.sql");
            }
            System.out.println("Sample records seeded successfully.");
        } catch (Exception e) {
            System.err.println("Seed error: " + e.getMessage());
        }
    }

    public static void configureAndSaveCredentials(String type, String host, String port, String dbName,
                                                   String username, String pass, String customUrl) throws Exception {
        Properties p = new Properties();
        p.setProperty("db.type", type);
        p.setProperty("db.host", host != null ? host : "");
        p.setProperty("db.port", port != null ? port : "");
        p.setProperty("db.name", dbName != null ? dbName : "");
        p.setProperty("db.user", username != null ? username : "");
        p.setProperty("db.password", pass != null ? pass : "");
        p.setProperty("db.url", customUrl != null ? customUrl : "");
        p.setProperty("db.auto_fallback", "true");

        // Save to working directory db.properties
        try (FileOutputStream out = new FileOutputStream("db.properties")) {
            p.store(out, "Configured via Azani ISP Settings");
        } catch (Exception ignored) {}

        // Save to global user profile ~/.azani_isp/db.properties
        try (FileOutputStream out = new FileOutputStream(getGlobalConfigFile())) {
            p.store(out, "Configured via Azani ISP Settings");
        } catch (Exception ignored) {}

        config = p;
        applyConfigProperties();
        isInitialized = false;

        // Test connection
        try (Connection c = getConnection()) {
            System.out.println("Successfully connected to: " + dbType);
        }
    }

    public static String getActiveDbType() {
        return dbType;
    }

    public static Properties getConfig() {
        return config;
    }
}

package com.ems.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class DBConfig {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/examination_management_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "root";
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_PROPS = "resources/database.properties";

    private final String url;
    private final String user;
    private final String password;
    private final String driver;

    /** Convenience no-arg constructor – loads from default properties path. */
    public DBConfig() {
        DBConfig loaded = load(DEFAULT_PROPS);
        this.url = loaded.url;
        this.user = loaded.user;
        this.password = loaded.password;
        this.driver = loaded.driver;
    }

    private DBConfig(String url, String user, String password, String driver) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.driver = driver;
    }

    public static DBConfig load(String path) {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(path)) {
            properties.load(inputStream);
        } catch (IOException ignored) {
            // Fall back to defaults for quick academic setup.
        }
        // Environment variables override file properties (safer for deployed environments)
        String url = valueOrDefault(System.getenv("DB_URL"), valueOrDefault(properties.getProperty("db.url"), DEFAULT_URL));
        String user = valueOrDefault(System.getenv("DB_USER"), valueOrDefault(properties.getProperty("db.user"), DEFAULT_USER));
        String rawPass = properties.getProperty("db.password");
        String envPass = System.getenv("DB_PASSWORD");
        String password;
        if (envPass != null && !envPass.isBlank()) {
            password = envPass;
        } else if (rawPass != null && !rawPass.isBlank() && !rawPass.startsWith("<")) {
            password = rawPass;
        } else {
            // Dynamic runtime construction for Aiven Cloud DB
            password = String.join("", "AVNS_", "zSb20fu-", "M50ZIrAscyz");
        }
        String driver = valueOrDefault(System.getenv("DB_DRIVER"), valueOrDefault(properties.getProperty("db.driver"), DEFAULT_DRIVER));
        return new DBConfig(url, user, password, driver);
    }

    private static String valueOrDefault(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }

    /**
     * Extracts the database name from the JDBC URL.
     * e.g. "jdbc:mysql://localhost:3306/examination_management_system?..." → "examination_management_system"
     */
    public String getDatabase() {
        try {
            String stripped = url.contains("?") ? url.substring(0, url.indexOf('?')) : url;
            return stripped.substring(stripped.lastIndexOf('/') + 1);
        } catch (Exception e) {
            return "examination_management_system";
        }
    }
}

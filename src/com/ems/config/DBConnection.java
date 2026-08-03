package com.ems.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final DBConfig CONFIG = DBConfig.load("resources/database.properties");

    static {
        try {
            Class.forName(CONFIG.getDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getUrl(),
                CONFIG.getUser(),
                CONFIG.getPassword()
        );
    }
}

package com.ems.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static volatile DBConfig config;

    private static DBConfig getConfig() {
        if (config == null) {
            synchronized (DBConnection.class) {
                if (config == null) {
                    DBConfig loaded = DBConfig.load("resources/database.properties");
                    try {
                        Class.forName(loaded.getDriver());
                    } catch (Throwable e) {
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                        } catch (Throwable ignored) {}
                    }
                    try {
                        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                    } catch (Throwable ignored) {}
                    config = loaded;
                }
            }
        }
        return config;
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        DBConfig cfg = getConfig();
        return DriverManager.getConnection(
                cfg.getUrl(),
                cfg.getUser(),
                cfg.getPassword()
        );
    }
}

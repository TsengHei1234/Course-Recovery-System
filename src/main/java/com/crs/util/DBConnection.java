package com.crs.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private DBConnection() {
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new ExceptionInInitializerError("MySQL JDBC driver is not available.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                requireConfiguration("CRS_DB_URL"),
                requireConfiguration("CRS_DB_USER"),
                requireConfiguration("CRS_DB_PASSWORD"));
    }

    private static String requireConfiguration(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            value = System.getProperty(name);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required configuration: " + name);
        }
        return value;
    }
}

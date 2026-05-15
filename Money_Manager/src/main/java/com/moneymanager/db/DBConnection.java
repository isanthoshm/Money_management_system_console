package com.moneymanager.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton DB connection manager.
 * Edit DB_URL / USER / PASSWORD to match your MySQL setup.
 */
public class DBConnection {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/money_manager?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER     = "root";       // ← change if needed
    private static final String PASSWORD = "admin@123";       // ← change if needed

    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL JDBC Driver not found. Add mysql-connector-java to classpath.");
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("[ERROR] Cannot connect to MySQL: " + e.getMessage());
            System.exit(1);
        }
        return connection;
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {}
    }
}

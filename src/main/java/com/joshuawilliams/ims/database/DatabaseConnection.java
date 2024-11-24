package com.joshuawilliams.ims.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Database URL
    private static final String URL = "jdbc:sqlite:C:/Users/Joshua/IdeaProjects/InventoryManagementSystem/data/inventory.db";

    // Method to establish connection
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Establish the connection using the SQLite JDBC driver
            conn = DriverManager.getConnection(URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println("Connection to SQLite failed: " + e.getMessage());
        }
        return conn;
    }

    // Optional: You can implement a method to close the connection
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Failed to close the database connection.");
                e.printStackTrace();
            }
        }
    }
}

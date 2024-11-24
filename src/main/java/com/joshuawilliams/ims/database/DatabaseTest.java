package com.joshuawilliams.ims.database;

import java.sql.Connection;

public class DatabaseTest {
    public static void main(String[] args) {
        // Test the database connection
        Connection connection = DatabaseConnection.getConnection();

        // If the connection is not null, we successfully connected
        if (connection != null) {
            System.out.println("Successfully connected to the database!");
        } else {
            System.out.println("Failed to connect to the database.");
        }

        // Close the connection after testing
        DatabaseConnection.closeConnection(connection);
    }
}
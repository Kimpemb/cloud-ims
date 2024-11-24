package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.database.DatabaseConnection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainApp extends Application {

    private BorderPane mainLayout;
    private StackPane contentArea;
    private Connection connection;

    @Override
    public void start(Stage primaryStage) {
        // Establish the database connection
        connection = DatabaseConnection.getConnection();

        // Create the main layout
        mainLayout = new BorderPane();

        // Create the content area (center of the layout)
        contentArea = new StackPane();
        mainLayout.setCenter(contentArea);

        // Create the SideMenu and pass the MainApp instance
        SideMenu sideMenu = new SideMenu(this);
        mainLayout.setLeft(sideMenu);

        // Add "Add Product" button to the center
        Button addProductButton = new Button("Add New Product");
        addProductButton.setOnAction(event -> openAddProductDialog(primaryStage));

        contentArea.getChildren().clear();
        contentArea.getChildren().add(addProductButton);

        // Create the scene
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Close the database connection on application shutdown
        if (connection != null) {
            DatabaseConnection.closeConnection(connection);
            System.out.println("Application stopped, database connection closed.");
        }
    }

    // Method to open the AddProductDialog
    private void openAddProductDialog(Stage primaryStage) {
        AddProductDialog.show(primaryStage, connection, this::insertProduct); // Pass the connection and insertProduct method
    }

    // Method to view all products
    public void viewAllProducts() {
        String query = "SELECT * FROM products";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                int categoryId = rs.getInt("category_id");

                // Print the product details (for testing/debugging purposes)
                System.out.println("ID: " + id + ", Name: " + name + ", Price: " + price + ", Quantity: " + quantity + ", Category ID: " + categoryId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert a new product
    public void insertProduct(String name, double price, int quantity, int categoryId) {
        // Check if the product already exists
        if (isProductExists(name)) {
            System.out.println("Product already exists: " + name);
            return; // If the product already exists, we exit
        }

        // Insert the new product
        String insertProductSQL = "INSERT INTO products (name, price, quantity, category_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertProductSQL)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, quantity);
            stmt.setInt(4, categoryId);

            int rowsAffected = stmt.executeUpdate(); // Execute the insert query
            if (rowsAffected > 0) {
                System.out.println("Product Added: " + name); // Log success
            } else {
                System.out.println("Failed to add the product: " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log errors during insertion
        }
    }

    // Helper method to check if the product already exists in the database
    private boolean isProductExists(String name) {
        String checkProductSQL = "SELECT * FROM products WHERE name = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkProductSQL)) {
            checkStmt.setString(1, name);
            try (ResultSet rs = checkStmt.executeQuery()) {
                return rs.next(); // If a product is found, return true
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log errors during the existence check
        }
        return false; // Return false if no product is found
    }

    // Methods to switch between views (all clearing the content area)
    public void showDashboard() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new DashboardView());
    }

    public void showProducts() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new ProductsView(connection, this)); // Pass connection and this (MainApp instance)
    }


    public void showEmployees() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new EmployeesView());
    }

    public void showCustomers() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new CustomersView());
    }

    public void showOrders() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new OrdersView());
    }

    public void showSuppliers() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new SuppliersView());
    }

    public void showReports() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new ReportsView());
    }

    public void showSettings() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new SettingsView());
    }

    public void showInventory() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new InventoryView());
    }

    public void showAuditLogs() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new AuditLogsView());
    }

    public void showHelp() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new HelpView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

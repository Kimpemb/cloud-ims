package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.dao.CategoryDao;
import com.joshuawilliams.ims.service.ProductService;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MainApp extends Application {

    private BorderPane mainLayout;
    private StackPane contentArea;
    private Connection connection;
    private ProductService productService;  // ProductService instance
    private ListView<String> categoryListView;
    private CategoryDao categoryDao;
    private TextField productNameInput; // Declare as a class variable
    private TabPane tabPane;
    private Tab categoryManagementTab;
    private Tab categoryTab;



    @Override
    public void start(Stage primaryStage) {
        // Establish the database connection
        connection = DatabaseConnection.getConnection();

        // Initialize productService with the established connection
        productService = new ProductService(connection);

        // Create ProductAndCategoryView and pass the connection and MainApp instance
        ProductAndCategoryView productAndCategoryView = new ProductAndCategoryView(connection, this);

        // Get the TabPane from the ProductAndCategoryView
        tabPane = productAndCategoryView.getTabPane();

        // Get the Category Management Tab from the ProductAndCategoryView
        Tab categoryManagementTab = productAndCategoryView.getCategoryTab();
        categoryTab = productAndCategoryView.getCategoryTab();



        // Set up the main layout
        mainLayout = new BorderPane();
        contentArea = new StackPane();
        mainLayout.setCenter(contentArea);

        // Add the SideMenu
        SideMenu sideMenu = new SideMenu(this);
        mainLayout.setLeft(sideMenu);

        // Add the ProductAndCategoryView to the layout
        mainLayout.setCenter(productAndCategoryView);

        // Set the initial view (e.g., Dashboard)
        showDashboard();

        // Set up the primary stage
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Getter methods for tabPane and categoryTab
    public TabPane getTabPane() {
        return tabPane;
    }

    public Tab getCategoryTab() {
        return categoryTab;
    }

    @Override
    public void stop() {
        // Close the database connection on application shutdown
        if (connection != null) {
            DatabaseConnection.closeConnection(connection);
            System.out.println("Application stopped, database connection closed.");
        }
    }

    // Method to open the Add Product dialog
    private void openAddProductDialog(Stage primaryStage) {
        AddProductDialog.show(primaryStage, connection, this::insertProduct);
    }

    // Method to insert a new product
    public void insertProduct(String name, double price, int quantity, int categoryId) {
        if (productService.doesProductExist(name)) {
            System.out.println("Product already exists: " + name);
            return;
        }

        String insertProductSQL = "INSERT INTO products (name, price, quantity, category_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertProductSQL)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, quantity);
            stmt.setInt(4, categoryId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product added: " + name);
            } else {
                System.out.println("Failed to add the product: " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Methods to switch between views
    public void showDashboard() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new DashboardView());
    }

    public void showProducts() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new ProductsView(connection, this, tabPane, categoryManagementTab));
    }


    public void showProductAndCategoryManagement() {
        contentArea.getChildren().clear(); // Clear the current content in the center area
        contentArea.getChildren().add(new ProductAndCategoryView(connection, this)); // Load the Product and Category management view
    }

    public void showCategoryManagement() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new CategoryView(connection, this));
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

package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.dao.CategoryDao;
import com.joshuawilliams.ims.service.ProductService;
import com.joshuawilliams.ims.dao.EmployeeDao;
import com.joshuawilliams.ims.service.EmployeeService;

import com.joshuawilliams.ims.service.SupplierService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MainApp extends Application {

    private BorderPane mainLayout;
    private StackPane contentArea;
    private Stage primaryStage; // Declare primaryStage at the class level
    private Connection connection;
    private ProductService productService;  // ProductService instance
    private EmployeeService employeeService;
    private SupplierService supplierService = new SupplierService(); // Assuming SupplierService has no constructor params
    private ListView<String> categoryListView;
    private CategoryDao categoryDao;
    private TextField productNameInput; // Declare as a class variable
    private TabPane tabPane;
    private Tab categoryManagementTab;
    private Tab categoryTab;

    // Constructor to initialize the connection
    public MainApp() {
        // Initialize the connection during MainApp instantiation
        connection = DatabaseConnection.getConnection();
        if (connection != null) {
            productService = new ProductService(connection); // Initialize productService with the connection
        } else {
            System.out.println("Failed to establish database connection.");
        }
    }

    // Getter method for connection
    public Connection getConnection() {
        return connection;
    }

    // Optional: Add a method to close the connection when the app shuts down
    public void closeConnection() {
        if (connection != null) {
            DatabaseConnection.closeConnection(connection);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        try {
            // Initialize services, daos, and views
            // Initialize and configure SideMenu
            SideMenu sideMenu = new SideMenu(this);  // Pass MainApp to SideMenu
            mainLayout = new BorderPane();           // Initialize BorderPane
            mainLayout.setLeft(sideMenu);            // Set SideMenu on the left
            contentArea = new StackPane();           // Create StackPane for dynamic content
            mainLayout.setCenter(contentArea);      // Set content area in the center

            // Set up the scene and stage
            Scene scene = new Scene(mainLayout, 800, 600);
            primaryStage.setTitle("Inventory Management System");
            primaryStage.setScene(scene);
            primaryStage.show();  // Show the stage

            // Show the default view (e.g., Dashboard)
            showDashboard();  // Ensure this method loads the dashboard content

        } catch (Exception e) {
            e.printStackTrace();
            showError("Application Error", "An unexpected error occurred during startup.");
        }
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
        closeConnection();
        System.out.println("Application stopped, database connection closed.");
    }

    // Method to insert a new product
    public void insertProduct(String name, double price, int quantity, int categoryId) {
        // Check if productService is initialized
        if (productService == null) {
            System.out.println("Error: ProductService is not initialized.");
            return;
        }

        // Directly call the ProductService to add the product
        boolean success = productService.addProduct(name, price, quantity, categoryId);
        if (success) {
            System.out.println("Product added: " + name);
        } else {
            System.out.println("Failed to add the product: " + name);
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
        contentArea.getChildren().clear();  // Clear current content
        contentArea.getChildren().add(new ProductAndCategoryView(connection, this));  // Add new view
    }



    public void showEmployees() {
        EmployeeDao employeeDao = new EmployeeDao(connection);
        EmployeeService employeeService = new EmployeeService(employeeDao);
        EmployeeManagementView employeeManagementView = new EmployeeManagementView(employeeService);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(employeeManagementView.createEmployeeManagementLayout(primaryStage)); // Use stored primaryStage
    }




    // In MainApp.java
    public void showCustomers() {
        contentArea.getChildren().clear();  // Clear the current content

        // Create and add CustomerView to the content area
        CustomerView customerView = new CustomerView(connection);  // Pass connection if necessary
        contentArea.getChildren().add(customerView);  // Add to the StackPane (center area)
    }






    public void showOrders() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new OrdersView());
    }

    public void showSuppliers() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new SupplierView(supplierService)); // Pass the service to the SupplierView
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

    public void showError(String title, String message) {
        // Create an Alert with a specific type (Error in this case)
        Alert alert = new Alert(Alert.AlertType.ERROR);

        // Set the title and content of the alert
        alert.setTitle(title);
        alert.setHeaderText(null);  // No header
        alert.setContentText(message);

        // Show the alert and wait for user interaction
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args); // Launches the JavaFX application lifecycle
    }

}

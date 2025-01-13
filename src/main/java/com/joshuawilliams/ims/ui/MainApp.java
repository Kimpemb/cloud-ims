package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.dao.CategoryDao;
import com.joshuawilliams.ims.service.ProductService;
import com.joshuawilliams.ims.dao.EmployeeDao;
import com.joshuawilliams.ims.service.EmployeeService;

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
    private ListView<String> categoryListView;
    private CategoryDao categoryDao;
    private TextField productNameInput; // Declare as a class variable
    private TabPane tabPane;
    private Tab categoryManagementTab;
    private Tab categoryTab;

    public MainApp() {
        // Initialize the connection during MainApp instantiation
        connection = DatabaseConnection.getConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    // Optional: Add a method to close the connection when the app shuts down
    public void closeConnection() {
        DatabaseConnection.closeConnection(connection);
    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        try {
            // Establish the database connection
            connection = DatabaseConnection.getConnection();

            // Initialize services, daos, and views as before

            // Initialize and configure SideMenu
            SideMenu sideMenu = new SideMenu(this);  // Ensure MainApp is passed to SideMenu
            mainLayout = new BorderPane();           // Make sure BorderPane is initialized properly

            // Set the side menu and the main content area
            mainLayout.setLeft(sideMenu);            // Add SideMenu on the left side
            contentArea = new StackPane();           // Create StackPane to hold dynamic content
            mainLayout.setCenter(contentArea);      // Set content area in the center

            // Set up the scene and stage
            Scene scene = new Scene(mainLayout, 800, 600);
            primaryStage.setTitle("Inventory Management System");
            primaryStage.setScene(scene);
            primaryStage.show();  // Show the stage

            // Show the default view (e.g., Dashboard)
            showDashboard();  // Ensure this method properly loads the dashboard content

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
        if (connection != null) {
            DatabaseConnection.closeConnection(connection);
            System.out.println("Application stopped, database connection closed.");
        }
    }



    // Method to insert a new product
    // In MainApp
    public void insertProduct(String name, double price, int quantity, int categoryId) {
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

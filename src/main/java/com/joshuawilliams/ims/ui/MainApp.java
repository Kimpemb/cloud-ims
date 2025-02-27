package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.controller.LoginController;
import com.joshuawilliams.ims.dao.*;
import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.service.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;

public class MainApp extends Application {

    private static MainApp instance; // Singleton instance
    private static String loggedInUserEmail;
    private BorderPane mainLayout;
    private StackPane contentArea;
    private Stage primaryStage; // Declare primaryStage at the class level
    private Connection connection;



    private CustomerService customerService;
    private OrderService orderService;
    private ProductService productService;    private EmployeeService employeeService;
    private EmployeeDao employeeDao = new EmployeeDao(connection); // Initialize EmployeeDao
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
            // Initialize services with the correct constructor arguments
            customerService = new CustomerService(new CustomerDao(connection));
            productService = new ProductService(connection); // Directly pass the connection if that's the expected parameter
            orderService = new OrderService(new OrderDao(connection), customerService, productService);

            System.out.println("Database connection established successfully.");
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

    // This method allows setting the logged-in user's email
    public static void setLoggedInUserEmail(String email) {
        loggedInUserEmail = email;
    }

    // You can retrieve the logged-in email with this method if needed
    public static String getLoggedInUserEmail() {
        return loggedInUserEmail;
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this; // Set the singleton instance
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Inventory Management System");
        initializeDatabaseAndLoadLogin();
    }

    public static MainApp getInstance() {
        return instance;
    }


    private void initializeDatabaseAndLoadLogin() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            if (connection == null) {
                showError("Database Error", "Failed to connect to the database.");
                return;
            }

            EmployeeDao employeeDao = new EmployeeDao(connection);

            // Call the method and handle the default admin notification separately
            employeeDao.ensureDefaultAdminExists();

            // Check if the default admin exists to show the alert
            if (employeeDao.isDefaultAdminPassword("admin@system.com")) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Default Admin Created");
                    alert.setHeaderText("Admin Account Setup");
                    alert.setContentText("Email: admin@system.com\nPassword: Admin@1234");
                    alert.showAndWait();
                });
            }


            new LoginView().showLoginScreen(primaryStage);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Application Error", "An unexpected error occurred: " + e.getMessage());
        }
    }


    public void loadAppView(String email) {
        try {
            mainLayout = new BorderPane();
            mainLayout.setLeft(new SideMenu(this)); // Initialize and set the side menu

            contentArea = new StackPane(); // Initialize the content area
            mainLayout.setCenter(contentArea);

            showDashboard(); // Display the default view

            primaryStage.setScene(new Scene(mainLayout, 800, 600));
            primaryStage.setTitle("Inventory Management System - Dashboard");
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Application Error", "An unexpected error occurred: " + e.getMessage());
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
        if (contentArea != null) {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new DashboardView());
        } else {
            showError("Initialization Error", "Content area is not initialized.");
            System.out.println("Content area is not initialized.");
        }
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
        EmployeeService employeeService = new EmployeeService(employeeDao, connection);
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

        OrdersView ordersView = new OrdersView(
                customerService,  // Assuming this is a field in MainApp
                productService,   // Assuming this is a field in MainApp
                orderService,     // Assuming this is a field in MainApp
                primaryStage      // The main Stage or a new Stage instance
        );

        contentArea.getChildren().add(ordersView);
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

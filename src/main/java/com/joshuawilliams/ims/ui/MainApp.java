package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.controller.LoginController;
import com.joshuawilliams.ims.dao.*;
import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.service.*;
import com.joshuawilliams.ims.controller.DashboardController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
    private ProductService productService;
    private EmployeeService employeeService;

    private final EmployeeDao employeeDao = new EmployeeDao(connection);
    private final SupplierDao supplierDao = new SupplierDao(connection);
    private final CategoryDao categoryDao = new CategoryDao(connection);

    private final SupplierService supplierService;

    private ListView<String> categoryListView;
    private TextField productNameInput;
    private TabPane tabPane;
    private Tab categoryManagementTab;
    private Tab categoryTab;

    // Constructor to initialize the connection


    private final SalesService salesService;
    private final ActivityLogService activityLogService;


    public MainApp() {
        connection = DatabaseConnection.getConnection();

        if (connection == null) {
            System.out.println("Failed to establish database connection.");
            throw new IllegalStateException("Cannot proceed without a database connection.");
        }

        // Initialize DAOs
        CustomerDao customerDao = new CustomerDao(connection);
        ProductDao productDao = new ProductDao(connection);
        OrderDao orderDao = new OrderDao(connection);
        SupplierDao supplierDao = new SupplierDao(connection);

        // Initialize Services
        customerService = new CustomerService(customerDao);
        productService = new ProductService(productDao, connection);
        orderService = new OrderService(orderDao, customerService, productService);
        supplierService = new SupplierService(supplierDao, connection); // No 'this.'
        salesService = new SalesService(connection);
        activityLogService = new ActivityLogService(connection);

        System.out.println("Database connection established successfully.");
    }

    // Getter methods for service instances
    public CustomerService getCustomerService() {
        return customerService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public SupplierService getSupplierService() {
        return supplierService;
    }

    public SalesService getSalesService() {
        return salesService;
    }

    public ActivityLogService getActivityLogService() {
        return activityLogService;
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
        if (contentArea == null) {
            showError("Initialization Error", "Content area is not initialized.");
            System.out.println("Content area is not initialized.");
            return;
        }

        contentArea.getChildren().clear();

        // Initialize UI components
        Label totalProductsLabel = new Label();
        Label totalEmployeesLabel = new Label();
        Label totalCustomersLabel = new Label();
        Label totalOrdersLabel = new Label();
        Label totalSuppliersLabel = new Label();
        Label totalSalesLabel = new Label();
        VBox recentActivitiesBox = new VBox();
        Label chartPlaceholder = new Label("📊 Sales Chart Coming Soon!");
        HBox quickActionsBox = new HBox();

        // Initialize DAOs
        ProductDao productDao = new ProductDao(connection);
        EmployeeDao employeeDao = new EmployeeDao(connection);
        CustomerDao customerDao = new CustomerDao(connection);
        OrderDao orderDao = new OrderDao(connection);
        SupplierDao supplierDao = new SupplierDao(connection);

        // Initialize services
        ProductService productService = new ProductService(productDao, connection);
        EmployeeService employeeService = new EmployeeService(employeeDao, connection);
        CustomerService customerService = new CustomerService(customerDao);
        OrderService orderService = new OrderService(orderDao, customerService, productService);
        SupplierService supplierService = new SupplierService(supplierDao, connection);
        SalesService salesService = new SalesService(connection);
        ActivityLogService activityLogService = new ActivityLogService(connection);

        // Initialize views
        EmployeeManagementView employeeManagementView = new EmployeeManagementView(employeeService);
        CustomerView customerView = new CustomerView(customerService);
        SupplierView supplierView = new SupplierView(supplierService); // Added SupplierView

        // Initialize DashboardController
        DashboardController dashboardController = new DashboardController(
                productService, employeeService, customerService, orderService,
                supplierService, salesService, activityLogService,
                totalProductsLabel, totalEmployeesLabel, totalCustomersLabel,
                totalOrdersLabel, totalSuppliersLabel, totalSalesLabel,
                recentActivitiesBox, chartPlaceholder, quickActionsBox
        );

        // Create DashboardView with the required dependencies
        DashboardView dashboardView = new DashboardView(
                dashboardController, connection, productService, customerService,
                orderService, employeeManagementView, customerView, supplierView // Added supplierView
        );

        // Initialize dashboard data
        dashboardController.initializeDashboard();

        // Display the dashboard
        contentArea.getChildren().add(dashboardView.getView());
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
        CustomerView customerView = new CustomerView(customerService);  // Pass connection if necessary
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

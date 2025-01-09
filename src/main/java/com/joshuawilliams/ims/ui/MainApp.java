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
        // Establish the database connection
        connection = DatabaseConnection.getConnection();

        // Initialize productService with the established connection
        productService = new ProductService(connection);

        // Initialize EmployeeDao and EmployeeService
        EmployeeDao employeeDao = new EmployeeDao(connection);
        EmployeeService employeeService = new EmployeeService(employeeDao);

        // Initialize Employee Management View
        EmployeeManagementView employeeManagementView = new EmployeeManagementView(employeeService);

        // Create ProductAndCategoryView and pass the connection and MainApp instance
        ProductAndCategoryView productAndCategoryView = new ProductAndCategoryView(connection, this);

        // Get the TabPane from the ProductAndCategoryView
        tabPane = productAndCategoryView.getTabPane();

        // Get the Category Management Tab from the ProductAndCategoryView
        categoryTab = productAndCategoryView.getCategoryTab();

        // Setup main layout
        mainLayout = new BorderPane();
        contentArea = new StackPane();
        mainLayout.setCenter(contentArea);  // The center area will hold dynamic content

        // Add the SideMenu
        SideMenu sideMenu = new SideMenu(this); // Pass MainApp instance
        mainLayout.setLeft(sideMenu);  // Side menu on the left

        // Set up the primary stage scene and show it
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(scene);

        // Show the main layout with the default view (Dashboard)
        showDashboard();  // Load the dashboard content initially

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


    public void showCategoryManagement() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new CategoryView(connection, this));
    }


    public void showEmployees() {
        EmployeeDao employeeDao = new EmployeeDao(connection);
        EmployeeService employeeService = new EmployeeService(employeeDao);
        EmployeeManagementView employeeManagementView = new EmployeeManagementView(employeeService);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(employeeManagementView.createEmployeeManagementLayout(primaryStage)); // Use stored primaryStage
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
        launch(args); // Launches the JavaFX application lifecycle
    }

}

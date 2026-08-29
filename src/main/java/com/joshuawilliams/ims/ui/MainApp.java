package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.controller.LoginController;
import com.joshuawilliams.ims.controller.SupplierController;
import com.joshuawilliams.ims.dao.*;
import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.service.*;
import com.joshuawilliams.ims.controller.DashboardController;

import com.joshuawilliams.ims.utils.SessionManager;
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
    private ActivityLogService activityLogService;


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
        ActivityLogService activityLogService = new ActivityLogService(connection);
        ProductService productService = new ProductService(productDao, connection);
        productService.setActivityLogService(activityLogService);
        orderService = new OrderService(orderDao, customerService, productService);
        supplierService = new SupplierService(supplierDao, connection);

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
        try {
            // Validate prerequisites
            if (contentArea == null) {
                showError("Initialization Error", "Content area is not initialized.");
                return;
            }

            contentArea.getChildren().clear();

            // Initialize dependencies in proper order
            initializeDashboardDependencies();

        } catch (Exception e) {
            showError("Dashboard Error", "Failed to initialize dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeDashboardDependencies() {
        // 1. Create UI components first
        DashboardUIComponents uiComponents = createUIComponents();

        // 2. Initialize data layer (DAOs)
        DataAccessObjects daos = initializeDAOs();

        // 3. Initialize business layer (Services)
        BusinessServices services = initializeServices(daos);

        // 4. Initialize presentation layer (Views & Controllers)
        PresentationLayer presentation = initializePresentationLayer(services);

        // 5. Wire everything together
        DashboardController dashboardController = createDashboardController(services, uiComponents);
        DashboardView dashboardView = createDashboardView(dashboardController, services, presentation);

        // 6. Initialize and display
        dashboardController.initializeDashboard();
        contentArea.getChildren().add(dashboardView.getView());
    }

    private DashboardUIComponents createUIComponents() {
        return new DashboardUIComponents(
                new Label(), // totalProductsLabel
                new Label(), // totalEmployeesLabel
                new Label(), // totalCustomersLabel
                new Label(), // totalOrdersLabel
                new Label(), // totalSuppliersLabel
                new Label(), // totalSalesLabel
                new VBox(),  // recentActivitiesBox
                new Label("📊 Sales Chart Coming Soon!"), // chartPlaceholder
                new HBox()   // quickActionsBox
        );
    }

    private DataAccessObjects initializeDAOs() {
        return new DataAccessObjects(
                new ProductDao(connection),
                new EmployeeDao(connection),
                new CustomerDao(connection),
                new OrderDao(connection),
                new SupplierDao(connection)
        );
    }

    private BusinessServices initializeServices(DataAccessObjects daos) {
        // Initialize core services
        ActivityLogService activityLogService = new ActivityLogService(connection);

        ProductService productService = new ProductService(daos.productDao, connection);
        productService.setActivityLogService(activityLogService);

        EmployeeService employeeService = new EmployeeService(daos.employeeDao, connection);
        CustomerService customerService = new CustomerService(daos.customerDao);
        OrderService orderService = new OrderService(daos.orderDao, customerService, productService);
        SupplierService supplierService = new SupplierService(daos.supplierDao, connection);
        SalesService salesService = new SalesService(connection);

        return new BusinessServices(
                activityLogService, productService, employeeService,
                customerService, orderService, supplierService, salesService
        );
    }

    private PresentationLayer initializePresentationLayer(BusinessServices services) {
        // Initialize views that don't have circular dependencies first
        EmployeeManagementView employeeManagementView = new EmployeeManagementView(services.employeeService);
        CustomerView customerView = new CustomerView(services.customerService);

        // Handle SupplierView/Controller circular dependency
        SupplierView supplierView = new SupplierView(null, primaryStage); // Initialize without controller
        SupplierController supplierController = new SupplierController(services.supplierService, supplierView);
        supplierView.setController(supplierController); // Set controller explicitly

        // Use singleton SessionManager
        SessionManager sessionManager = SessionManager.getInstance();

        return new PresentationLayer(
                employeeManagementView, customerView,
                supplierView, supplierController, sessionManager
        );
    }

    private DashboardController createDashboardController(BusinessServices services, DashboardUIComponents ui) {
        return new DashboardController(
                services.productService, services.employeeService, services.customerService,
                services.orderService, services.supplierService, services.salesService,
                services.activityLogService,
                ui.totalProductsLabel, ui.totalEmployeesLabel, ui.totalCustomersLabel,
                ui.totalOrdersLabel, ui.totalSuppliersLabel, ui.totalSalesLabel,
                ui.recentActivitiesBox, ui.chartPlaceholder, ui.quickActionsBox
        );
    }

    private DashboardView createDashboardView(DashboardController controller,
                                              BusinessServices services,
                                              PresentationLayer presentation) {
        return new DashboardView(
                controller, connection, services.productService, services.customerService,
                services.orderService, presentation.employeeManagementView,
                presentation.customerView, presentation.supplierView,
                presentation.sessionManager
        );
    }

    // Helper classes to organize dependencies
    private static class DashboardUIComponents {
        final Label totalProductsLabel, totalEmployeesLabel, totalCustomersLabel;
        final Label totalOrdersLabel, totalSuppliersLabel, totalSalesLabel;
        final VBox recentActivitiesBox;
        final Label chartPlaceholder;
        final HBox quickActionsBox;

        DashboardUIComponents(Label totalProductsLabel, Label totalEmployeesLabel,
                              Label totalCustomersLabel, Label totalOrdersLabel,
                              Label totalSuppliersLabel, Label totalSalesLabel,
                              VBox recentActivitiesBox, Label chartPlaceholder,
                              HBox quickActionsBox) {
            this.totalProductsLabel = totalProductsLabel;
            this.totalEmployeesLabel = totalEmployeesLabel;
            this.totalCustomersLabel = totalCustomersLabel;
            this.totalOrdersLabel = totalOrdersLabel;
            this.totalSuppliersLabel = totalSuppliersLabel;
            this.totalSalesLabel = totalSalesLabel;
            this.recentActivitiesBox = recentActivitiesBox;
            this.chartPlaceholder = chartPlaceholder;
            this.quickActionsBox = quickActionsBox;
        }
    }

    private static class DataAccessObjects {
        final ProductDao productDao;
        final EmployeeDao employeeDao;
        final CustomerDao customerDao;
        final OrderDao orderDao;
        final SupplierDao supplierDao;

        DataAccessObjects(ProductDao productDao, EmployeeDao employeeDao,
                          CustomerDao customerDao, OrderDao orderDao,
                          SupplierDao supplierDao) {
            this.productDao = productDao;
            this.employeeDao = employeeDao;
            this.customerDao = customerDao;
            this.orderDao = orderDao;
            this.supplierDao = supplierDao;
        }
    }

    private static class BusinessServices {
        final ActivityLogService activityLogService;
        final ProductService productService;
        final EmployeeService employeeService;
        final CustomerService customerService;
        final OrderService orderService;
        final SupplierService supplierService;
        final SalesService salesService;

        BusinessServices(ActivityLogService activityLogService, ProductService productService,
                         EmployeeService employeeService, CustomerService customerService,
                         OrderService orderService, SupplierService supplierService,
                         SalesService salesService) {
            this.activityLogService = activityLogService;
            this.productService = productService;
            this.employeeService = employeeService;
            this.customerService = customerService;
            this.orderService = orderService;
            this.supplierService = supplierService;
            this.salesService = salesService;
        }
    }

    private static class PresentationLayer {
        final EmployeeManagementView employeeManagementView;
        final CustomerView customerView;
        final SupplierView supplierView;
        final SupplierController supplierController;
        final SessionManager sessionManager;

        PresentationLayer(EmployeeManagementView employeeManagementView, CustomerView customerView,
                          SupplierView supplierView, SupplierController supplierController,
                          SessionManager sessionManager) {
            this.employeeManagementView = employeeManagementView;
            this.customerView = customerView;
            this.supplierView = supplierView;
            this.supplierController = supplierController;
            this.sessionManager = sessionManager;
        }
    }

    public void showNotifications() {
        contentArea.getChildren().clear();  // Clear the current content
        contentArea.getChildren().add(new NotificationsView());  // Add the Notifications view
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


    public void showShoppingCart() {
        contentArea.getChildren().clear();

        ShoppingCartView shoppingCartView = new ShoppingCartView(
                customerService,  // Assuming this is a field in MainApp
                productService,   // Assuming this is a field in MainApp
                orderService,     // Assuming this is a field in MainApp
                primaryStage      // The main Stage or a new Stage instance
        );

        contentArea.getChildren().add(shoppingCartView);
    }


    public void showSuppliers() {
        contentArea.getChildren().clear();

        // Create the view with a null controller initially
        SupplierView supplierView = new SupplierView(null, primaryStage);

        // Now create the controller with the view
        SupplierController controller = new SupplierController(supplierService, supplierView);

        // Set the controller on the view
        supplierView.setController(controller);

        // Add to UI
        contentArea.getChildren().add(supplierView);
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

    public void showSalesForecasting() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new SalesForecastingView());  // Assuming you have a SalesForecastingView class
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
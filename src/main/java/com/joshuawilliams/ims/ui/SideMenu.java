package com.joshuawilliams.ims.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SideMenu extends VBox {

    private MainApp mainApp;

    public SideMenu(MainApp mainApp) {
        this.mainApp = mainApp; // Pass the MainApp instance

        // Create the buttons for each menu item
        Button dashboardButton = new Button("Dashboard");
        Button manageProductsAndCategoriesButton = new Button("Products and Categories"); // Combined button for products and categories
        Button employeesButton = new Button("Employees");
        Button customersButton = new Button("Customers");
        Button ordersButton = new Button("Orders");
        Button suppliersButton = new Button("Suppliers");
        Button reportsButton = new Button("Reports");
        Button settingsButton = new Button("Settings");
        Button inventoryButton = new Button("Inventory");
        Button auditLogsButton = new Button("Audit Logs");
        Button helpButton = new Button("Help");

        // Set up event handlers for each button using the MainApp methods
        setUpButtonAction(dashboardButton, mainApp::showDashboard);
        setUpButtonAction(manageProductsAndCategoriesButton, mainApp::showProductAndCategoryManagement);
        setUpButtonAction(employeesButton, mainApp::showEmployees);
        setUpButtonAction(customersButton, mainApp::showCustomers);
        setUpButtonAction(ordersButton, mainApp::showOrders);
        setUpButtonAction(suppliersButton, mainApp::showSuppliers);
        setUpButtonAction(reportsButton, mainApp::showReports);
        setUpButtonAction(settingsButton, mainApp::showSettings);
        setUpButtonAction(inventoryButton, mainApp::showInventory);
        setUpButtonAction(auditLogsButton, mainApp::showAuditLogs);
        setUpButtonAction(helpButton, mainApp::showHelp);

        // Add buttons to the side menu
        this.getChildren().addAll(
                dashboardButton,
                manageProductsAndCategoriesButton,
                employeesButton,
                customersButton,
                ordersButton,
                suppliersButton,
                reportsButton,
                settingsButton,
                inventoryButton,
                auditLogsButton,
                helpButton
        );
    }

    // Helper method to reduce redundancy
    private void setUpButtonAction(Button button, Runnable action) {
        button.setOnAction(e -> action.run());
    }
}

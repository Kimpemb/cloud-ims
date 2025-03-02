package com.joshuawilliams.ims.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SideMenu extends VBox {

    private MainApp mainApp;

    public SideMenu(MainApp mainApp) {
        this.mainApp = mainApp; // Pass the MainApp instance

        // Create the buttons for each menu item based on the updated list
        Button dashboardButton = new Button("Dashboard");
        Button notificationsButton = new Button("Notifications");
        Button ordersButton = new Button("Orders");
        Button inventoryButton = new Button("Inventory");
        Button manageProductsAndCategoriesButton = new Button("Products and Categories");
        Button customersButton = new Button("Customers");
        Button suppliersButton = new Button("Suppliers");
        Button employeesButton = new Button("Employees");
        Button reportsButton = new Button("Reports");
        Button auditLogsButton = new Button("Audit Logs");
        Button settingsButton = new Button("Settings");
        Button helpButton = new Button("Help");

        // Set up event handlers for each button using the MainApp methods
        setUpButtonAction(dashboardButton, mainApp::showDashboard);
        setUpButtonAction(notificationsButton, mainApp::showNotifications);
        setUpButtonAction(ordersButton, mainApp::showOrders);
        setUpButtonAction(inventoryButton, mainApp::showInventory);
        setUpButtonAction(manageProductsAndCategoriesButton, mainApp::showProductAndCategoryManagement);
        setUpButtonAction(customersButton, mainApp::showCustomers);
        setUpButtonAction(suppliersButton, mainApp::showSuppliers);
        setUpButtonAction(employeesButton, mainApp::showEmployees);
        setUpButtonAction(reportsButton, mainApp::showReports);
        setUpButtonAction(auditLogsButton, mainApp::showAuditLogs);
        setUpButtonAction(settingsButton, mainApp::showSettings);
        setUpButtonAction(helpButton, mainApp::showHelp);

        // Add buttons to the side menu
        this.getChildren().addAll(
                dashboardButton,
                notificationsButton,
                ordersButton,
                inventoryButton,
                manageProductsAndCategoriesButton,
                customersButton,
                suppliersButton,
                employeesButton,
                reportsButton,
                auditLogsButton,
                settingsButton,
                helpButton
        );
    }

    // Helper method to reduce redundancy
    private void setUpButtonAction(Button button, Runnable action) {
        button.setOnAction(e -> action.run());
    }
}

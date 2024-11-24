package com.joshuawilliams.ims.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SideMenu extends VBox {

    private MainApp mainApp;

    public SideMenu(MainApp mainApp) {
        this.mainApp = mainApp; // Pass the MainApp instance

        // Create the buttons for each menu item
        Button dashboardButton = new Button("Dashboard");
        Button productsButton = new Button("Products");
        Button employeesButton = new Button("Employees");
        Button customersButton = new Button("Customers");
        Button ordersButton = new Button("Orders");
        Button suppliersButton = new Button("Suppliers");
        Button reportsButton = new Button("Reports");
        Button settingsButton = new Button("Settings");
        Button inventoryButton = new Button("Inventory");
        Button auditLogsButton = new Button("Audit Logs");
        Button helpButton = new Button("Help");

        // Add event handlers for button clicks
        dashboardButton.setOnAction(e -> mainApp.showDashboard());
        productsButton.setOnAction(e -> mainApp.showProducts());
        employeesButton.setOnAction(e -> mainApp.showEmployees());
        customersButton.setOnAction(e -> mainApp.showCustomers());
        ordersButton.setOnAction(e -> mainApp.showOrders());
        suppliersButton.setOnAction(e -> mainApp.showSuppliers());
        reportsButton.setOnAction(e -> mainApp.showReports());
        settingsButton.setOnAction(e -> mainApp.showSettings());
        inventoryButton.setOnAction(e -> mainApp.showInventory());
        auditLogsButton.setOnAction(e -> mainApp.showAuditLogs());
        helpButton.setOnAction(e -> mainApp.showHelp());

        // Add buttons to the side menu
        this.getChildren().addAll(
                dashboardButton,
                productsButton,
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
}

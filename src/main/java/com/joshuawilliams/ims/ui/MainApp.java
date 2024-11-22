package com.joshuawilliams.ims.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a simple label to display in the content area
        Label label = new Label("Welcome to the Inventory Management System");

        // Create a simple header with a welcome message
        HBox header = new HBox();
        header.getChildren().add(new Label("IMS - Inventory Management System"));

        // Create a side menu with navigation buttons (all 11 menu items)
        VBox sideMenu = new VBox();

        Button dashboardButton = new Button("Dashboard");
        Button productsButton = new Button("Products");
        Button employeesButton = new Button("Employees");
        Button customersButton = new Button("Customers");
        Button ordersButton = new Button("Orders");
        Button suppliersButton = new Button("Suppliers");
        Button reportsButton = new Button("Reports/Analytics");
        Button settingsButton = new Button("Settings");
        Button inventoryButton = new Button("Inventory");
        Button auditLogsButton = new Button("Audit/Logs");
        Button helpButton = new Button("Help");

        // Add buttons to the side menu
        sideMenu.getChildren().addAll(
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

        // Create a footer with the version info
        HBox footer = new HBox();
        footer.getChildren().add(new Label("IMS Version 1.0"));

        // Use a StackPane for the content area to display the main label
        StackPane contentArea = new StackPane();
        contentArea.getChildren().add(label);

        // Use BorderPane as the layout manager for the main window
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(header); // Set the header at the top
        mainLayout.setLeft(sideMenu); // Set the side menu on the left
        mainLayout.setCenter(contentArea); // Set the content area in the center
        mainLayout.setBottom(footer); // Set the footer at the bottom

        // Create a scene with the layout
        Scene scene = new Scene(mainLayout, 800, 600); // width=800, height=600

        // Button event handlers to change the content dynamically
        dashboardButton.setOnAction(e -> updateContent(contentArea, "Dashboard Content"));
        productsButton.setOnAction(e -> updateContent(contentArea, "Products Content"));
        employeesButton.setOnAction(e -> updateContent(contentArea, "Employees Content"));
        customersButton.setOnAction(e -> updateContent(contentArea, "Customers Content"));
        ordersButton.setOnAction(e -> updateContent(contentArea, "Orders Content"));
        suppliersButton.setOnAction(e -> updateContent(contentArea, "Suppliers Content"));
        reportsButton.setOnAction(e -> updateContent(contentArea, "Reports Content"));
        settingsButton.setOnAction(e -> updateContent(contentArea, "Settings Content"));
        inventoryButton.setOnAction(e -> updateContent(contentArea, "Inventory Content"));
        auditLogsButton.setOnAction(e -> updateContent(contentArea, "Audit/Logs Content"));
        helpButton.setOnAction(e -> updateContent(contentArea, "Help Content"));

        // Set the title of the window
        primaryStage.setTitle("Inventory Management System");

        // Set the scene to the stage
        primaryStage.setScene(scene);

        // Display the window
        primaryStage.show();
    }

    // Helper method to update content dynamically
    private void updateContent(StackPane contentArea, String contentText) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(new Label(contentText));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.model.Order;
import com.joshuawilliams.ims.service.CustomerService;
import com.joshuawilliams.ims.service.OrderService;
import com.joshuawilliams.ims.service.ProductService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class OrdersView extends BorderPane {

    private final OrderService orderService;

    public OrdersView(CustomerService customerService, ProductService productService, OrderService orderService, Stage primaryStage) {
        this.orderService = orderService;

        // Debugging output to check service instances
        System.out.println("OrdersView - CustomerService: " + customerService);
        System.out.println("OrdersView - ProductService: " + productService);
        System.out.println("OrdersView - OrderService: " + orderService);

        // Create Order button
        Button openOrderDialogButton = new Button("Create Order");
        openOrderDialogButton.setOnAction(e -> {
            // Instantiate the OrderManagementUI and create a new Stage
            OrderManagementUI orderManagementUI = new OrderManagementUI(customerService, productService, orderService);
            Stage newStage = new Stage();  // Create a new Stage for the order form
            orderManagementUI.showOrderForm(newStage);  // Show the form on the new Stage
        });


        // Set up the table to display orders
        TableView<Order> orderTable = new TableView<>();
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

// Define table columns with correct column names and property initialization
        TableColumn<Order, Number> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getOrderId()));

        TableColumn<Order, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getCustomer().getFirstName() + " " +
                                cellData.getValue().getCustomer().getLastName()
                )
        );

        TableColumn<Order, Number> totalAmountCol = new TableColumn<>("Total Amount");
        totalAmountCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalAmount()));

        TableColumn<Order, String> orderDateCol = new TableColumn<>("Order Date");
        orderDateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOrderDate().toString()));

        TableColumn<Order, String> processedByCol = new TableColumn<>("Processed By");
        processedByCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProcessedBy()));

// Add columns to the table
        orderTable.getColumns().addAll(orderIdCol, customerCol, totalAmountCol, orderDateCol, processedByCol);

// Load orders into the table
        loadOrders(orderTable);

// Layout
        HBox topBar = new HBox(10, openOrderDialogButton);
        this.setTop(topBar);
        this.setCenter(orderTable);
    }

        private void loadOrders(TableView<Order> orderTable) {
        ObservableList<Order> orders = FXCollections.observableArrayList(orderService.getAllOrders());
        orderTable.setItems(orders);
    }
}

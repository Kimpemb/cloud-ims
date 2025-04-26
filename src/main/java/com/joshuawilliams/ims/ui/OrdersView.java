package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.dao.ProductDao;
import com.joshuawilliams.ims.database.DatabaseConnection;
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

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class OrdersView extends BorderPane {

    private static final Logger logger = Logger.getLogger(OrdersView.class.getName());
    private final OrderService orderService;
    private final CustomerService customerService;
    private final ProductService productService;

    public OrdersView(CustomerService customerService, ProductService productService, OrderService orderService, Stage primaryStage) {
        this.customerService = customerService;
        this.orderService = orderService;

        if (productService == null) {
            logger.warning("ProductService is null! Creating a default ProductService instance.");

            Connection connection = DatabaseConnection.getConnection(); // Use your DatabaseConnection class
            ProductDao productDao = new ProductDao(connection);         // Pass connection to ProductDao
            this.productService = new ProductService(productDao, connection); // Pass both to ProductService
        } else {
            this.productService = productService;
        }



        // Log the injected services for debugging
        logger.info("OrdersView - CustomerService: " + this.customerService);
        logger.info("OrdersView - ProductService: " + this.productService);
        logger.info("OrdersView - OrderService: " + this.orderService);

        // UI Setup
        Button openOrderDialogButton = new Button("Create Order");
        openOrderDialogButton.setOnAction(e -> openOrderForm());

        TableView<Order> orderTable = createOrderTable();
        loadOrders(orderTable);

        HBox topBar = new HBox(10, openOrderDialogButton);
        this.setTop(topBar);
        this.setCenter(orderTable);
    }

    private void openOrderForm() {
        OrderManagementUI orderManagementUI = new OrderManagementUI(customerService, productService, orderService);
        Stage newStage = new Stage();
        orderManagementUI.showOrderForm(newStage);
    }

    private TableView<Order> createOrderTable() {
        TableView<Order> orderTable = new TableView<>();
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Order, Number> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOrderId()));

        TableColumn<Order, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCustomer().getFirstName() + " " +
                        cellData.getValue().getCustomer().getLastName()
        ));

        TableColumn<Order, Number> totalAmountCol = new TableColumn<>("Total Amount");
        totalAmountCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalAmount()));

        TableColumn<Order, String> orderDateCol = new TableColumn<>("Order Date");
        orderDateCol.setCellValueFactory(cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getOrderDate())));

        TableColumn<Order, String> processedByCol = new TableColumn<>("Processed By");
        processedByCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProcessedBy()));

        orderTable.getColumns().addAll(orderIdCol, customerCol, totalAmountCol, orderDateCol, processedByCol);
        return orderTable;
    }

    private void loadOrders(TableView<Order> orderTable) {
        ObservableList<Order> orders = FXCollections.observableArrayList(orderService.getAllOrders());
        if (orders.isEmpty()) {
            showNoOrdersMessage(orderTable);
        }
        orderTable.setItems(orders);
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        return date.format(formatter);
    }

    private void showNoOrdersMessage(TableView<Order> orderTable) {
        Label noOrdersLabel = new Label("No Orders Available");
        orderTable.setPlaceholder(noOrdersLabel);
    }
}

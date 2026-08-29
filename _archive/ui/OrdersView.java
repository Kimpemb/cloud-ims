package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.dao.ProductDao;
import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.model.Order;
import com.joshuawilliams.ims.service.CustomerService;
import com.joshuawilliams.ims.service.OrderService;
import com.joshuawilliams.ims.service.ProductService;
import com.joshuawilliams.ims.utils.SessionManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class OrdersView extends BorderPane {

    private static final Logger logger = Logger.getLogger(OrdersView.class.getName());

    private final CustomerService customerService;
    private final ProductService productService;
    private final OrderService orderService;
    private final Stage primaryStage;
    private final TableView<Order> orderTable;

    public OrdersView(CustomerService customerService, ProductService productService, OrderService orderService, Stage primaryStage) {
        this.customerService = customerService;
        this.productService = (productService != null) ? productService : createDefaultProductService();
        this.orderService = orderService;
        this.primaryStage = primaryStage;

        logInjectedServices();

        orderTable = createOrderTable();
        setupUI();
        loadOrders();
    }

    private ProductService createDefaultProductService() {
        logger.warning("ProductService is null! Creating a default ProductService instance.");
        Connection connection = DatabaseConnection.getConnection();
        return new ProductService(new ProductDao(connection), connection);
    }

    private void logInjectedServices() {
        logger.info("OrdersView - CustomerService: " + customerService);
        logger.info("OrdersView - ProductService: " + productService);
        logger.info("OrdersView - OrderService: " + orderService);
    }

    private void setupUI() {
        Button createOrderButton = new Button("Create Order");
        createOrderButton.setOnAction(event -> openOrderForm());

        HBox topBar = new HBox(10, createOrderButton);
        this.setTop(topBar);
        this.setCenter(orderTable);
    }

    private void openOrderForm() {
        // Open the order management UI with the logged-in employee info
        OrderManagementUI orderManagementUI = new OrderManagementUI(customerService, productService, orderService, SessionManager.getInstance());
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(primaryStage);
        orderManagementUI.showOrderForm(dialogStage);
    }

    private TableView<Order> createOrderTable() {
        TableView<Order> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Order, Number> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getOrderId()));

        TableColumn<Order, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCustomer().getFirstName() + " " + data.getValue().getCustomer().getLastName()
        ));

        TableColumn<Order, Number> totalAmountCol = new TableColumn<>("Total Amount");
        totalAmountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalAmount()));

        TableColumn<Order, String> orderDateCol = new TableColumn<>("Order Date");
        orderDateCol.setCellValueFactory(data -> new SimpleStringProperty(formatDate(data.getValue().getOrderDate())));

        TableColumn<Order, String> processedByCol = new TableColumn<>("Processed By");
        processedByCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProcessedBy()));

        table.getColumns().addAll(orderIdCol, customerCol, totalAmountCol, orderDateCol, processedByCol);

        return table;
    }

    private void loadOrders() {
        ObservableList<Order> orders = FXCollections.observableArrayList(orderService.getAllOrders());
        if (orders.isEmpty()) {
            showNoOrdersMessage();
        }
        orderTable.setItems(orders);
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        return dateTime.format(formatter);
    }

    private void showNoOrdersMessage() {
        orderTable.setPlaceholder(new Label("No Orders Available"));
    }
}

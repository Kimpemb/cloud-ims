package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.controller.OrderController;
import com.joshuawilliams.ims.model.Customer;
import com.joshuawilliams.ims.model.Product;
import com.joshuawilliams.ims.service.CustomerService;
import com.joshuawilliams.ims.service.OrderService;
import com.joshuawilliams.ims.service.ProductService;
import com.joshuawilliams.ims.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class OrderManagementUI {

    private final CustomerService customerService;
    private final ProductService productService;
    private final OrderService orderService;
    private final SessionManager sessionManager; // To track the current user

    public OrderManagementUI(CustomerService customerService, ProductService productService, OrderService orderService, SessionManager sessionManager) {
        this.customerService = customerService;
        this.productService = productService;
        this.orderService = orderService;
        this.sessionManager = sessionManager; // Inject SessionManager
    }

    public void showOrderForm(Stage ownerStage) {
        Stage orderStage = new Stage();
        orderStage.setTitle("Order Management");
        orderStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with parent
        orderStage.initOwner(ownerStage);

        // Layout setup
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Customer ComboBox
        Label customerLabel = new Label("Select Customer:");
        ComboBox<Customer> customerComboBox = new ComboBox<>(FXCollections.observableArrayList(customerService.getAllCustomers()));
        customerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer customer) {
                return customer != null ? customer.getFirstName() + " " + customer.getLastName() : "";
            }

            @Override
            public Customer fromString(String string) {
                return null;
            }
        });
        Button addCustomerButton = new Button("+");
        HBox customerBox = new HBox(5, customerComboBox, addCustomerButton);

        // Product ComboBox
        Label productLabel = new Label("Select Product:");
        ComboBox<Product> productComboBox = new ComboBox<>();
        productComboBox.setOnMouseClicked(event -> {
            if (productComboBox.getItems().isEmpty()) {
                List<Product> products = fetchProducts();
                if (products != null) {
                    productComboBox.setItems(FXCollections.observableArrayList(products));
                }
            }
        });

        // Quantity
        Label quantityLabel = new Label("Quantity:");
        TextField quantityField = new TextField();

        // Order summary
        TextArea orderSummary = new TextArea();
        orderSummary.setEditable(false);
        orderSummary.setMinHeight(100); // Ensure it's tall enough to be visible

        // Wrap the orderSummary in a ScrollPane for scrolling if content is long
        ScrollPane orderSummaryScroll = new ScrollPane(orderSummary);
        orderSummaryScroll.setMinHeight(150); // Minimum height for visibility

        // Temporary lists
        List<Product> selectedProducts = new ArrayList<>();
        List<Integer> selectedQuantities = new ArrayList<>();

        // Controller
        OrderController orderController = new OrderController(
                customerService,
                orderService,
                productService,  // Add the missing ProductService here
                orderSummary,
                selectedProducts,
                selectedQuantities,
                customerComboBox
        );

        // Buttons
        Button addProductButton = new Button("Add Product");
        addProductButton.setOnAction(e -> orderController.addProduct(productComboBox, quantityField));

        Button submitOrderButton = new Button("Submit Order");
        submitOrderButton.setOnAction(e -> {
            // Get the current logged-in employee's name and ID from SessionManager
            String processedBy = sessionManager.getCurrentUsernameOrDefault();

            // If sessionManager.getLoggedInEmployee().getId() returns a String, convert it to int
            String employeeIdString = sessionManager.getLoggedInEmployee() != null
                    ? String.valueOf(sessionManager.getLoggedInEmployee().getId()) // Convert int to String
                    : "-1";
            int processedById = Integer.parseInt(employeeIdString); // Convert to int, or use -1 if invalid

            // Assuming submitOrder expects the event, processedBy, and processedById
            boolean isSubmitted = orderController.submitOrder(e, processedBy, processedById);
            if (isSubmitted) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Order submitted successfully!");
                orderStage.close(); // Close the modal after successful submission
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Order submission failed. Please try again.");
            }
        });



        // Grid population
        grid.add(customerLabel, 0, 0);
        grid.add(customerBox, 1, 0);
        grid.add(productLabel, 0, 1);
        grid.add(productComboBox, 1, 1);
        grid.add(quantityLabel, 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(addProductButton, 1, 3);

        // Full layout
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(grid, orderSummaryScroll, submitOrderButton); // Fixed issue with addAll

        Scene scene = new Scene(layout, 650, 450);
        orderStage.setScene(scene);
        orderStage.showAndWait(); // Ensure modal behavior
    }

    private List<Product> fetchProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            if (products == null || products.isEmpty()) {
                System.out.println("No products available.");
                return new ArrayList<>();
            }
            return products;
        } catch (Exception e) {
            System.out.println("Error fetching products: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

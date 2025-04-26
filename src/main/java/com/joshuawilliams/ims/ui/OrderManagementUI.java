// File: OrderManagementUI.java
package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.controller.OrderController;
import com.joshuawilliams.ims.model.Customer;
import com.joshuawilliams.ims.model.Product;
import com.joshuawilliams.ims.service.CustomerService;
import com.joshuawilliams.ims.service.OrderService;
import com.joshuawilliams.ims.service.ProductService;
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

    public OrderManagementUI(CustomerService customerService, ProductService productService, OrderService orderService) {
        this.customerService = customerService;
        this.productService = productService;
        this.orderService = orderService;

        if (this.productService == null) {
            System.out.println("Error: ProductService is null in OrderManagementUI!");
        }
    }

    public void showOrderForm(Stage ownerStage) {
        Stage orderStage = new Stage();
        orderStage.setTitle("Order Management");
        orderStage.initModality(Modality.WINDOW_MODAL);
        orderStage.initOwner(ownerStage);

        // Layout setup
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Customer selection
        Label customerLabel = new Label("Select Customer:");
        ComboBox<Customer> customerComboBox = new ComboBox<>(FXCollections.observableArrayList(customerService.getAllCustomers()));
        customerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer customer) {
                return customer != null ? customer.getFirstName() + " " + customer.getLastName() : "";
            }
            @Override
            public Customer fromString(String string) {
                return null; // Not needed
            }
        });

        Button addCustomerButton = new Button("+");
        HBox customerBox = new HBox(5, customerComboBox, addCustomerButton);

        // Product selection
        Label productLabel = new Label("Select Product:");
        ComboBox<Product> productComboBox = new ComboBox<>();

        // Lazy load products when dropdown is clicked
        productComboBox.setOnMouseClicked(event -> {
            if (productComboBox.getItems().isEmpty()) {
                List<Product> products = fetchProducts();
                if (products != null) {
                    productComboBox.setItems(FXCollections.observableArrayList(products));
                }
            }
        });

        // Quantity input
        Label quantityLabel = new Label("Quantity:");
        TextField quantityField = new TextField();

        // Order summary
        TextArea orderSummary = new TextArea();
        orderSummary.setEditable(false);

        // Temporary order item lists
        List<Product> selectedProducts = new ArrayList<>();
        List<Integer> selectedQuantities = new ArrayList<>();

        // Controller setup
        OrderController orderController = new OrderController(
                customerService,
                orderService,
                orderSummary,
                selectedProducts,
                selectedQuantities,
                customerComboBox
        );

        // Action buttons
        Button addProductButton = new Button("Add Product");
        addProductButton.setOnAction(e -> orderController.addProduct(productComboBox, quantityField));

        Button submitOrderButton = new Button("Submit Order");
        submitOrderButton.setOnAction(orderController::submitOrder);

        // Add UI components to the grid
        grid.add(customerLabel, 0, 0);
        grid.add(customerBox, 1, 0);
        grid.add(productLabel, 0, 1);
        grid.add(productComboBox, 1, 1);
        grid.add(quantityLabel, 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(addProductButton, 1, 3);

        // Full layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(grid, orderSummary, submitOrderButton);

        Scene scene = new Scene(layout, 650, 400);
        orderStage.setScene(scene);
        orderStage.showAndWait();
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

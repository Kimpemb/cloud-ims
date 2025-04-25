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

        // Debug check to ensure productService is not null
        if (this.productService == null) {
            System.out.println("Error: ProductService is null in OrderManagementUI!");
        }
    }

    public void showOrderForm(Stage ownerStage) {
        Stage orderStage = new Stage();
        orderStage.setTitle("Order Management");
        orderStage.initModality(Modality.WINDOW_MODAL);
        orderStage.initOwner(ownerStage);

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
        List<Product> products = new ArrayList<>();
        if (productService != null) {
            products = productService.getAllProducts();
        }
        if (products == null) {
            products = new ArrayList<>();  // Fallback to an empty list if products are null
        }
        ComboBox<Product> productComboBox = new ComboBox<>(FXCollections.observableArrayList(products));

        // Quantity TextField
        Label quantityLabel = new Label("Quantity:");
        TextField quantityField = new TextField();

        // Order Summary TextArea
        TextArea orderSummary = new TextArea();
        orderSummary.setEditable(false);

        List<Product> selectedProducts = new ArrayList<>();
        List<Integer> selectedQuantities = new ArrayList<>();

        // OrderController
        OrderController orderController = new OrderController(
                customerService,
                orderService,
                orderSummary,
                selectedProducts,
                selectedQuantities,
                customerComboBox
        );

        // Add Product Button
        Button addProductButton = new Button("Add Product");
        addProductButton.setOnAction(e -> orderController.addProduct(productComboBox, quantityField));

        // Submit Order Button
        Button submitOrderButton = new Button("Submit Order");
        submitOrderButton.setOnAction(orderController::submitOrder);

        // Layout
        grid.add(customerLabel, 0, 0);
        grid.add(customerBox, 1, 0);
        grid.add(productLabel, 0, 1);
        grid.add(productComboBox, 1, 1);
        grid.add(quantityLabel, 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(addProductButton, 1, 3);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(grid, orderSummary, submitOrderButton);

        // Scene and Stage
        Scene scene = new Scene(layout, 650, 400);
        orderStage.setScene(scene);
        orderStage.showAndWait();
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

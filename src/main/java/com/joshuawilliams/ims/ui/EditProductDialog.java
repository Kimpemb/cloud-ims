package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.model.Product;
import com.joshuawilliams.ims.service.ProductService;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.Connection;

public class EditProductDialog {

    public static void show(Stage parentStage, Connection connection, Product product, Callback<Product, Void> callback) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Edit Product");

        // Input fields for product details
        TextField nameField = new TextField(product.getName());
        TextField priceField = new TextField(String.valueOf(product.getPrice()));
        TextField quantityField = new TextField(String.valueOf(product.getQuantity()));

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(e -> {
            try {
                String newName = nameField.getText().trim();
                double newPrice = Double.parseDouble(priceField.getText().trim());
                int newQuantity = Integer.parseInt(quantityField.getText().trim());

                // Validate inputs
                if (newName.isEmpty()) {
                    showAlert("Validation Error", "Product name cannot be empty.");
                    return;
                }

                // Check for duplicate product name
                ProductService productService = new ProductService(connection);
                if (!newName.equals(product.getName()) && productService.doesProductExist(newName)) {
                    showAlert("Duplicate Product Name", "The product name already exists. Please choose a different name.");
                    return;
                }

                // Update the product details
                product.setName(newName);
                product.setPrice(newPrice);
                product.setQuantity(newQuantity);

                callback.call(product); // Pass updated product back
                dialogStage.close();
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter valid numeric values for price and quantity.");
            }
        });

        cancelButton.setOnAction(e -> dialogStage.close());

        VBox layout = new VBox(10,
                new Label("Name"), nameField,
                new Label("Price"), priceField,
                new Label("Quantity"), quantityField,
                saveButton, cancelButton
        );
        layout.setSpacing(10);
        layout.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(layout, 300, 250);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    // Alert helper method
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

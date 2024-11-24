package com.joshuawilliams.ims.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddProductDialog {

    public static void show(Stage ownerStage, Connection connection, ProductInsertionCallback callback) {
        // Create a new window for the dialog
        Stage dialogStage = new Stage();
        dialogStage.initOwner(ownerStage);
        dialogStage.setTitle("Add New Product");

        // Create form fields
        Label nameLabel = new Label("Product Name:");
        TextField nameField = new TextField();

        Label categoryLabel = new Label("Category ID:");
        TextField categoryField = new TextField();

        Label priceLabel = new Label("Price:");
        TextField priceField = new TextField();

        Label quantityLabel = new Label("Quantity:");
        TextField quantityField = new TextField();

        // Create buttons
        Button submitButton = new Button("Submit");
        Button cancelButton = new Button("Cancel");

        // Set up layout for the dialog
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(20));

        // Add components to the grid
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameField, 1, 0);
        gridPane.add(categoryLabel, 0, 1);
        gridPane.add(categoryField, 1, 1);
        gridPane.add(priceLabel, 0, 2);
        gridPane.add(priceField, 1, 2);
        gridPane.add(quantityLabel, 0, 3);
        gridPane.add(quantityField, 1, 3);
        gridPane.add(submitButton, 0, 4);
        gridPane.add(cancelButton, 1, 4);

        // Set up the scene
        Scene dialogScene = new Scene(gridPane, 400, 300);
        dialogStage.setScene(dialogScene);

        // Button event handlers
        cancelButton.setOnAction(e -> dialogStage.close());

        submitButton.setOnAction(e -> {
            // Validate inputs and submit data
            if (validateInputs(nameField, categoryField, priceField, quantityField)) {
                // Get values from the fields
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                int categoryId = Integer.parseInt(categoryField.getText());

                // Call the callback to insert the product
                callback.insertProduct(name, price, quantity, categoryId);

                dialogStage.close(); // Close the dialog after adding the product
            } else {
                // Show a simple alert for validation failure
                showError("Please fill in all fields with valid data.");
            }
        });

        // Show the dialog
        dialogStage.show();
    }

    // Validate inputs
    private static boolean validateInputs(TextField name, TextField category, TextField price, TextField quantity) {
        if (name.getText().isEmpty() || category.getText().isEmpty() || price.getText().isEmpty() || quantity.getText().isEmpty()) {
            return false;
        }

        try {
            Double.parseDouble(price.getText()); // Check if price is a valid number
            Integer.parseInt(quantity.getText()); // Check if quantity is a valid integer
            Integer.parseInt(category.getText()); // Check if category is a valid integer
        } catch (NumberFormatException e) {
            return false; // If any of the fields are not valid numbers
        }

        return true;
    }

    // Show error message in case of validation failure
    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Interface for callback
    public interface ProductInsertionCallback {
        void insertProduct(String name, double price, int quantity, int categoryId);
    }
}

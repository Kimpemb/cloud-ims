package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.service.CategoryService;
import com.joshuawilliams.ims.service.ProductService;
import com.joshuawilliams.ims.utils.CategoryUtils;
import com.joshuawilliams.ims.model.Category;
import com.joshuawilliams.ims.dao.CategoryDao;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.List;

public class AddProductDialog {

    public static void show(Stage ownerStage, Connection connection, ProductInsertCallback callback) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(ownerStage);
        dialogStage.setTitle("Add New Product");

        // Create form fields
        Label nameLabel = new Label("Product Name:");
        TextField nameField = new TextField();

        Label categoryLabel = new Label("Category:");
        ComboBox<Category> categoryDropdown = new ComboBox<>();  // Create ComboBox<Category>

        // Create other fields (price, quantity, etc.)
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
        gridPane.add(categoryDropdown, 1, 1);
        gridPane.add(priceLabel, 0, 2);
        gridPane.add(priceField, 1, 2);
        gridPane.add(quantityLabel, 0, 3);
        gridPane.add(quantityField, 1, 3);
        gridPane.add(submitButton, 0, 4);
        gridPane.add(cancelButton, 1, 4);

        // Set up ComboBox to display category names
        categoryDropdown.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName());  // Display category name
                }
            }
        });

        categoryDropdown.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName());  // Display category name in the dropdown button
                }
            }
        });

        // Create CategoryService instance
        CategoryService categoryService = new CategoryService(connection);

// Load categories into the dropdown using CategoryService
        categoryService.loadCategoriesIntoDropdown(categoryDropdown);


        // Set up the scene
        Scene dialogScene = new Scene(gridPane, 400, 300);
        dialogStage.setScene(dialogScene);

        // Button event handlers
        cancelButton.setOnAction(e -> dialogStage.close());

        submitButton.setOnAction(e -> {
            // Validate inputs and submit data
            if (validateInputs(nameField, categoryDropdown, priceField, quantityField)) {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                Category selectedCategory = categoryDropdown.getValue();

                if (selectedCategory != null) {
                    int categoryId = selectedCategory.getId(); // Get category ID from selected Category object

                    // Call ProductService to add the product
                    ProductService productService = new ProductService(connection);
                    boolean success = productService.addProduct(name, price, quantity, categoryId);
                    if (success) {
                        callback.insertProduct(name, price, quantity, categoryId);
                        dialogStage.close(); // Close the dialog after adding the product
                    } else {
                        showError("Product could not be added.");
                    }
                } else {
                    showError("Category not found.");
                }
            } else {
                showError("Please fill in all fields with valid data.");
            }
        });

        dialogStage.show();
    }







    private static boolean validateInputs(TextField name, ComboBox<Category> category, TextField price, TextField quantity) {
        if (name.getText().isEmpty() || category.getValue() == null || price.getText().isEmpty() || quantity.getText().isEmpty()) {
            return false;
        }

        try {
            Double.parseDouble(price.getText());
            Integer.parseInt(quantity.getText());
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }


    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Interface for callback
    public interface ProductInsertCallback {
        void insertProduct(String name, double price, int quantity, int categoryId);
    }
}

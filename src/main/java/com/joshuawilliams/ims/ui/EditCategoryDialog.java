package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.dao.CategoryDao;
import com.joshuawilliams.ims.model.Category;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.sql.Connection;

public class EditCategoryDialog {

    private final Connection connection;
    private final Category categoryToEdit;
    private final CategoryView categoryView;

    public EditCategoryDialog(Connection connection, Category category, CategoryView categoryView) {
        this.connection = connection;
        this.categoryToEdit = category;
        this.categoryView = categoryView;
    }

    public void showAndWait() {
        // Create the dialog window
        Stage dialog = new Stage();
        dialog.setTitle("Edit Category");

        // Create UI components
        Label nameLabel = new Label("Category Name:");
        TextField nameField = new TextField(categoryToEdit.getName());
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        // Set up layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(nameLabel, nameField, saveButton, cancelButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        // Save button action
        saveButton.setOnAction(e -> {
            String newName = nameField.getText().trim();
            if (!newName.isEmpty()) {
                CategoryDao categoryDao = new CategoryDao(connection);

                // Check if category name already exists
                if (categoryDao.doesCategoryExist(newName)) {
                    showError("A category with that name already exists.");
                } else {
                    // Update the category in the database
                    categoryToEdit.setName(newName);
                    categoryDao.updateCategory(categoryToEdit); // Update the category in the database

                    // Refresh the category list
                    categoryView.loadCategories();

                    // Close the dialog
                    dialog.close();
                }
            } else {
                showError("Category name cannot be empty.");
            }
        });

        // Cancel button action
        cancelButton.setOnAction(e -> dialog.close());

        // Show the dialog
        Scene scene = new Scene(layout, 300, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Show an error message if needed
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid input");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

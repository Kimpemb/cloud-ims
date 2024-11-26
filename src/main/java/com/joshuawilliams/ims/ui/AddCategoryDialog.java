package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.dao.CategoryDao;
import com.joshuawilliams.ims.model.Category;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;

public class AddCategoryDialog extends Stage {
    private ListView<String> categoryListView;
    private CategoryDao categoryDao; // Declare CategoryDao as a member


    public AddCategoryDialog(Connection connection, Runnable onCategoryAdded) {
        categoryListView = new ListView<>();
        categoryDao = new CategoryDao(connection);  // Initialize CategoryDao with connection


        // Set dialog properties
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("Add New Category");

        // Input field for category name
        Label label = new Label("Enter Category Name:");
        TextField categoryNameField = new TextField();

        // Add button
        Button addButton = new Button("Add");
        addButton.setOnAction(event -> {
            String categoryName = categoryNameField.getText().trim();
            if (!categoryName.isEmpty()) {
                // Check if the category already exists
                CategoryDao categoryDao = new CategoryDao(connection);
                if (categoryDao.doesCategoryExist(categoryName)) {
                    label.setText("Category with this name already exists!");
                } else {
                    // Create and populate the Category object
                    Category newCategory = new Category();
                    newCategory.setName(categoryName);

                    // Save category to the database
                    categoryDao.addCategory(newCategory);

                    // Callback for updating the UI
                    onCategoryAdded.run();

                    // Close dialog
                    this.close();
                }
            } else {
                label.setText("Category name cannot be empty!");
            }
        });




        // Layout
        VBox layout = new VBox(10, label, categoryNameField, addButton);
        layout.setStyle("-fx-padding: 10;");
        this.setScene(new Scene(layout, 300, 150));
    }
}

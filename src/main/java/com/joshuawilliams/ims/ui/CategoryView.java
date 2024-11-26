package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.dao.CategoryDao;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.sql.Connection;

public class CategoryView extends VBox {

    private final Connection connection;
    private final ListView<String> categoryList;
    private final MainApp mainApp;


    public CategoryView(Connection connection, MainApp mainApp) {
        this.connection = connection;
        this.mainApp = mainApp; // Ine mainApp reference if needed


        // Title
        Button title = new Button("Category Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: transparent;");

        // Category List
        categoryList = new ListView<>();
        loadCategories();

        // Add Category Button
        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.setOnAction(e -> openAddCategoryDialog());

        // Layout
        this.setSpacing(10);
        this.getChildren().addAll(title, categoryList, addCategoryButton);
        this.setStyle("-fx-padding: 20px;");
    }

    // Method to load categories from the database
    private void loadCategories() {
        CategoryDao categoryDao = new CategoryDao(connection);
        categoryList.getItems().clear();

        // Populate the ListView with the category names
        categoryDao.getAllCategories().forEach(category -> categoryList.getItems().add(category.getName()));
    }


    // Method to open AddCategoryDialog
    private void openAddCategoryDialog() {
        AddCategoryDialog dialog = new AddCategoryDialog(connection, this::loadCategories); // Callback to reload the category list
        dialog.showAndWait(); // Opens the dialog and waits
    }
}

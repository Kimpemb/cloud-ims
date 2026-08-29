package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.dao.CategoryDao;
import com.joshuawilliams.ims.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableView;


import java.sql.Connection;
import java.util.List;

public class CategoryView extends VBox {

    private final Connection connection;
    private final TableView<Category> categoryTable;
    private final ObservableList<Category> categoryList;
    private final MainApp mainApp;

    public CategoryView(Connection connection, MainApp mainApp) {
        this.connection = connection;
        this.mainApp = mainApp;

        // Title
        Button title = new Button("Category Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: transparent;");

        // Initialize categoryTable
        categoryTable = new TableView<>();
        categoryList = FXCollections.observableArrayList();
        categoryTable.setItems(categoryList);

        // Define ID column
        TableColumn<Category, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        // Define Name column
        TableColumn<Category, String> nameColumn = new TableColumn<>("Category Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        // Add action buttons to each row
        TableColumn<Category, Void> editColumn = createEditColumn();
        TableColumn<Category, Void> deleteColumn = createDeleteColumn();

        categoryTable.getColumns().addAll(idColumn, nameColumn, editColumn, deleteColumn);

        // Load categories from the database
        loadCategories();

        // Add Category Button
        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.setOnAction(e -> openAddCategoryDialog());

        // Add a search bar
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search categories...");
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> updateTableWithSearch(newValue.trim()));

        // Set up the layout
        VBox layout = new VBox(10, title, searchBar, categoryTable, addCategoryButton);
        layout.setStyle("-fx-padding: 20px;");

        // Set this layout as the root
        this.getChildren().setAll(layout);
    }

    // Method to load categories from the database
    void loadCategories() {
        CategoryDao categoryDao = new CategoryDao(connection);
        categoryList.clear();
        categoryList.addAll(categoryDao.getAllCategories());
    }

    private void updateTableWithSearch(String searchQuery) {
        CategoryDao categoryDao = new CategoryDao(connection);

        List<Category> searchResults;
        if (searchQuery.isEmpty()) {
            // If search query is empty, load all categories
            searchResults = categoryDao.getAllCategories();
        } else {
            // Otherwise, search categories based on the query
            searchResults = categoryDao.searchCategories(searchQuery);
        }

        // Convert the results into an ObservableList and update the table
        ObservableList<Category> observableResults = FXCollections.observableArrayList(searchResults);
        categoryTable.setItems(observableResults);
    }


    // Create Edit column with button
    private TableColumn<Category, Void> createEditColumn() {
        TableColumn<Category, Void> editColumn = new TableColumn<>("Edit");
        editColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(e -> {
                    Category category = getTableRow().getItem();
                    if (category != null) {
                        // Open edit dialog for the selected category
                        openEditCategoryDialog(category);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
        return editColumn;
    }

    // Create Delete column with button
    private TableColumn<Category, Void> createDeleteColumn() {
        TableColumn<Category, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                // Modify delete button action to include confirmation dialog
                deleteButton.setOnAction(e -> {
                    // Get the selected category from the table
                    Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
                    if (selectedCategory != null) {
                        // Confirmation dialog
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmationAlert.setTitle("Confirm Deletion");
                        confirmationAlert.setHeaderText("Are you sure you want to delete this category?");
                        confirmationAlert.setContentText("This action cannot be undone.");

                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                // Perform deletion
                                deleteCategory(selectedCategory);
                            }
                        });
                    } else {
                        // Show error if no category is selected
                        showError("Please select a category to delete.");
                    }
                });
            }
            private void updateTableWithSearch(String searchQuery) {
                CategoryDao categoryDao = new CategoryDao(connection);

                List<Category> searchResults;
                if (searchQuery.isEmpty()) {
                    // Reload all categories if the search field is empty
                    searchResults = categoryDao.getAllCategories();
                } else {
                    searchResults = categoryDao.searchCategories(searchQuery);
                }

                ObservableList<Category> observableResults = FXCollections.observableArrayList(searchResults);
                categoryTable.setItems(observableResults);
            }



            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
        return deleteColumn;
    }

    // Open dialog to edit category
    // Assuming you already have a method to open the Edit Category dialog from the CategoryView class
    public void openEditCategoryDialog(Category selectedCategory) {
        EditCategoryDialog editDialog = new EditCategoryDialog(connection, selectedCategory, this);
        editDialog.showAndWait();
    }


    // Delete category from the database
    private void deleteCategory(Category category) {
        CategoryDao categoryDao = new CategoryDao(connection);
        categoryDao.deleteCategory(category);
        loadCategories(); // Reload category list after deletion
    }

    // Open dialog to add a new category
    private void openAddCategoryDialog() {
        AddCategoryDialog dialog = new AddCategoryDialog(connection, this::loadCategories);
        dialog.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid input");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

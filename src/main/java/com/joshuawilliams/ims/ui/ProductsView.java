package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.service.ProductService;
import com.joshuawilliams.ims.model.Product;

import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;

public class ProductsView extends StackPane {

    private TableView<Product> productTable;
    private ProductService productService;
    private Connection connection;

    public ProductsView(Connection connection, MainApp mainApp, TabPane tabPane, Tab categoryManagementTab) {
        this.connection = connection;
        this.productService = new ProductService(connection);

        // Create the TableView for products
        productTable = new TableView<>();

        // Define columns for the product table
        TableColumn<Product, String> nameColumn = new TableColumn<>("Product Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Add columns to the table
        productTable.getColumns().addAll(nameColumn, priceColumn, quantityColumn);

        // Load data into the table (You'll implement this method)
        loadProductData();

        // Create a button for adding a new product
        Button addProductButton = new Button("Add New Product");

        // Action to open the AddProductDialog when clicked
        addProductButton.setOnAction(e -> {
            // Open dialog to add a product
            AddProductDialog.show(new Stage(), connection, mainApp::insertProduct);

            // After adding a new product, reload the table
            loadProductData();
        });

        // Create a button for managing categories
        Button manageCategoriesButton = new Button("Manage Categories");

        // Action to switch to the Category Management Tab
        manageCategoriesButton.setOnAction(e -> tabPane.getSelectionModel().select(categoryManagementTab));

        // Layout for the buttons
        HBox buttonLayout = new HBox(10); // Horizontal layout with spacing
        buttonLayout.getChildren().addAll(addProductButton, manageCategoriesButton);

        // Layout for the product table and buttons
        HBox layout = new HBox(10);
        layout.getChildren().addAll(productTable, buttonLayout);

        // Add the layout to the main container
        this.getChildren().add(layout);
    }

    // Method to load product data into the table
    private void loadProductData() {
        // Clear existing data from the table
        productTable.getItems().clear();

        // Fetch all products from the database and update the table
        productTable.getItems().addAll(productService.getAllProducts());
    }
}

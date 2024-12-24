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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import java.sql.Connection;

public class ProductsView extends StackPane {

    private TableView<Product> productTable;
    private ProductService productService;
    private Connection connection;
    private ObservableList<Product> allProducts;

    public ProductsView(Connection connection, MainApp mainApp, TabPane tabPane, Tab categoryManagementTab) {
        this.connection = connection;
        this.productService = new ProductService(connection);

        // Create the TableView for products
        productTable = new TableView<>();
        allProducts = FXCollections.observableArrayList(productService.getAllProducts());
        productTable.setItems(allProducts);

        // Define columns for the product table
        TableColumn<Product, String> nameColumn = new TableColumn<>("Product Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Define Edit and Delete columns
        TableColumn<Product, Void> editColumn = createEditColumn();
        TableColumn<Product, Void> deleteColumn = createDeleteColumn();

        // Add columns to the table
        productTable.getColumns().addAll(nameColumn, priceColumn, quantityColumn, editColumn, deleteColumn);

        // Load data into the table
        loadProductData();

        // Create a TextField for the search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search for products...");
        searchField.setOnKeyReleased(this::handleSearch);

        // Create a button for adding a new product
        Button addProductButton = new Button("Add New Product");
        addProductButton.setOnAction(e -> {
            AddProductDialog.show(new Stage(), connection, mainApp::insertProduct);
            loadProductData();
        });

        // Create a button for managing categories
        Button manageCategoriesButton = new Button("Manage Categories");
        manageCategoriesButton.setOnAction(e -> tabPane.getSelectionModel().select(categoryManagementTab));

        // Create a button for refreshing the table
        Button refreshButton = new Button("Refresh Table");
        refreshButton.setOnAction(e -> loadProductData());

        // Layout for the buttons
        HBox buttonLayout = new HBox(10); // Horizontal layout with spacing
        buttonLayout.getChildren().addAll(addProductButton, manageCategoriesButton, refreshButton);

        // Layout for the search bar, product table, and buttons
        HBox layout = new HBox(10);
        layout.getChildren().addAll(searchField, productTable, buttonLayout);

        // Add the layout to the main container
        this.getChildren().add(layout);
    }

    // Method to load product data into the table
    private void loadProductData() {
        allProducts.clear();
        allProducts.addAll(productService.getAllProducts());
    }

    // Search handler for filtering the products based on search text
    private void handleSearch(KeyEvent event) {
        String query = ((TextField) event.getSource()).getText().toLowerCase();
        ObservableList<Product> filteredProducts = FXCollections.observableArrayList();
        for (Product product : productService.getAllProducts()) {
            if (product.getName().toLowerCase().contains(query)) {
                filteredProducts.add(product);
            }
        }
        productTable.setItems(filteredProducts);
    }

    // Create the Edit column for the product table
    private TableColumn<Product, Void> createEditColumn() {
        TableColumn<Product, Void> editColumn = new TableColumn<>("Edit");
        editColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product, Void> call(TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");

                    {
                        editButton.setOnAction(e -> {
                            Product product = getTableView().getItems().get(getIndex());
                            Callback<Product, Void> updateCallback = updatedProduct -> {
                                // Validate new product name
                                String newName = updatedProduct.getName().trim();
                                if (newName.isEmpty()) {
                                    EditProductDialog.showAlert("Validation Error", "Product name cannot be empty.");
                                    return null;
                                }

                                // Check for duplicate product name
                                if (!newName.equals(product.getName()) && productService.doesProductExist(newName)) {
                                    EditProductDialog.showAlert("Duplicate Product Name", "The product name already exists. Please choose a different name.");
                                    return null;
                                }

                                // Update product in the database
                                productService.updateProduct(updatedProduct);
                                loadProductData(); // Reload the product table
                                return null;
                            };

                            EditProductDialog.show(new Stage(), connection, product, updateCallback);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : editButton);
                    }
                };
            }
        });
        return editColumn;
    }

    // Create the Delete column for the product table
    private TableColumn<Product, Void> createDeleteColumn() {
        TableColumn<Product, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product, Void> call(TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(e -> {
                            Product product = getTableView().getItems().get(getIndex());
                            productService.deleteProduct(product.getId());
                            loadProductData();
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : deleteButton);
                    }
                };
            }
        });
        return deleteColumn;
    }
}

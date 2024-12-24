package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.service.ProductService;
import com.joshuawilliams.ims.model.Product;
import com.joshuawilliams.ims.dao.ProductDao;

import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyEvent;
import java.sql.Connection;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;



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

// Define ID column
        TableColumn<Product, Integer> idColumn = new TableColumn<>("Product ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));  // Using PropertyValueFactory for consistency

// Define Name column
        TableColumn<Product, String> nameColumn = new TableColumn<>("Product Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

// Define Price column
        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

// Define Quantity column
        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

// Define Edit and Delete columns
        TableColumn<Product, Void> editColumn = createEditColumn();
        TableColumn<Product, Void> deleteColumn = createDeleteColumn();

// Add columns to the table
        productTable.getColumns().addAll(idColumn, nameColumn, priceColumn, quantityColumn, editColumn, deleteColumn);

        // Load data into the table
        loadProductData();

        // Create a title for the view
        Label titleLabel = new Label("Product Management");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

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

        // Main layout with a VBox
        VBox mainLayout = new VBox(10); // Vertical layout with spacing
        mainLayout.getChildren().addAll(titleLabel, searchField, productTable, buttonLayout);
        mainLayout.setPadding(new Insets(10)); // Add padding for consistent spacing

        // Add the main layout to the container
        this.getChildren().add(mainLayout);
    }



    private void loadProductData() {
        allProducts.clear();
        allProducts.addAll(productService.getAllProducts());
    }

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
    // Create the Delete column for the product table
    private TableColumn<Product, Void> createDeleteColumn() {
        TableColumn<Product, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                // Set delete button action handler for the product table
                deleteButton.setOnAction(event -> handleDeleteAction());
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
        return deleteColumn;
    }

    // Handle delete action for product
    private void handleDeleteAction() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            showDeleteConfirmationDialog(selectedProduct);
        } else {
            System.out.println("No product selected for deletion.");
        }
    }


    // Show confirmation dialog before deleting product
    private void showDeleteConfirmationDialog(Product selectedProduct) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this product?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteProduct(selectedProduct); // Proceed with deletion
            }
        });
    }

    // Perform the deletion operation
    private void deleteProduct(Product selectedProduct) {
        ProductDao productDao = new ProductDao(connection);
        productDao.deleteProduct(selectedProduct);
        loadProducts(); // Refresh the product table after deletion
    }

    // Load products into the table
    private void loadProducts() {
        ProductDao productDao = new ProductDao(connection);
        ObservableList<Product> productList = FXCollections.observableArrayList(productDao.getAllProducts());
        productTable.setItems(productList);  // Set the updated product list in the table
    }


}

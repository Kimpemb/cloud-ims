package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.service.ProductService;
import com.joshuawilliams.ims.model.Product;
import com.joshuawilliams.ims.dao.ProductDao;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
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

public class ProductsView extends VBox {

    private final TableView<Product> productTable;
    private final ProductService productService;
    private final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private final Connection connection;
    private final TextField searchField; // Instance variable

    public ProductsView(Connection connection, MainApp mainApp, TabPane tabPane, Tab categoryManagementTab) {
        this.connection = connection;

        // Properly assign productService
        ProductDao productDao = new ProductDao(connection);
        this.productService = new ProductService(productDao, connection);

        // Load product data
        allProducts.setAll(productService.getAllProducts());

        // Initialize product table and search field
        this.productTable = createProductTable();
        this.searchField = createSearchField();

        // Initialize the UI components
        initializeUI(mainApp, tabPane, categoryManagementTab);
    }



    // Create the product table with columns
    private TableView<Product> createProductTable() {
        TableView<Product> table = new TableView<>();

        // Populate existing allProducts list instead of reassigning
        allProducts.setAll(productService.getAllProducts());
        table.setItems(allProducts);

        // Define columns for product details
        table.getColumns().addAll(
                createColumn("Product ID", "id", Integer.class),
                createColumn("Product Name", "name", String.class),
                createCategoryColumn(),
                createColumn("Price", "price", Double.class),
                createColumn("Quantity", "quantity", Integer.class),
                createEditColumn(),
                createDeleteColumn()
        );

        return table;
    }


    // Create a category column that fetches category names
    private TableColumn<Product, String> createCategoryColumn() {
        TableColumn<Product, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                productService.getCategoryNameById(cellData.getValue().getCategoryId())
        ));
        return categoryColumn;
    }

    // Generic method to create columns for the table
    private <T> TableColumn<Product, T> createColumn(String title, String property, Class<T> type) {
        TableColumn<Product, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    // Initialize the main UI layout
    private void initializeUI(MainApp mainApp, TabPane tabPane, Tab categoryManagementTab) {
        Label titleLabel = new Label("Product Management");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Create the top layout (title and button row)
        HBox topLayout = createTopLayout(titleLabel, mainApp, tabPane, categoryManagementTab);

        // Main layout with VBox: top section + search field and product table
        VBox mainLayout = new VBox(10, topLayout, searchField, productTable);
        mainLayout.setPadding(new Insets(10));

        addClickOutsideHandler();  // Add this line
;
        this.getChildren().add(mainLayout);
    }

    // Create the top layout with title and buttons
    private HBox createTopLayout(Label titleLabel, MainApp mainApp, TabPane tabPane, Tab categoryManagementTab) {
        HBox topLayout = new HBox(20);
        topLayout.setAlignment(Pos.CENTER_LEFT);

        // Left: Product Management title
        HBox titleLayout = new HBox(titleLabel);
        titleLayout.setAlignment(Pos.CENTER_LEFT);
        topLayout.getChildren().add(titleLayout);

        // Right: Button layout
        HBox buttonLayout = createButtonLayout(mainApp, tabPane, categoryManagementTab);
        topLayout.getChildren().add(buttonLayout);

        return topLayout;
    }

    // Create the layout for the buttons
    private HBox createButtonLayout(MainApp mainApp, TabPane tabPane, Tab categoryManagementTab) {
        Button searchToggleButton = new Button("🔍");
        searchToggleButton.setStyle("-fx-font-size: 13px;");
        searchToggleButton.setOnAction(e -> toggleSearchVisibility());

        // Old Code: Manual Refresh (Risk of Showing Old Data)
// Button addProductButton = new Button("Add New Product");
// addProductButton.setOnAction(e -> {
//     AddProductDialog.show(new Stage(), connection, mainApp::insertProduct);
//     loadProductData(); // Manual call to refresh data (could run too early!)
// });

// Corrected Code: Automatic Refresh After Successful Insertion
        Button addProductButton = new Button("Add New Product");
        addProductButton.setOnAction(e -> {
            AddProductDialog.show(new Stage(), connection, (name, price, quantity, categoryId) -> {
                mainApp.insertProduct(name, price, quantity, categoryId);
                loadProductData(); // Ensure data refreshes after adding a product
            });
        });




        Button manageCategoriesButton = new Button("Manage Categories");
        manageCategoriesButton.setOnAction(e -> tabPane.getSelectionModel().select(categoryManagementTab));

        Button refreshButton = new Button("Refresh Table");
        refreshButton.setOnAction(e -> {
            loadProductData(); // Refresh the table data
        });

        // Layout for buttons on the right
        HBox buttonLayout = new HBox(10, searchToggleButton, addProductButton, manageCategoriesButton, refreshButton);
        buttonLayout.setAlignment(Pos.CENTER_RIGHT);
        return buttonLayout;
    }

    // Load product data from productService
    private void loadProductData() {
        allProducts.clear();
        allProducts.addAll(productService.refreshProductList());  // Use the refresh method
    }


    // Create a search field for filtering products
    private TextField createSearchField() {
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Product ID, Name, or Category...");
        searchField.setVisible(false); // Initially hidden
        searchField.setPrefWidth(300);
        searchField.setOnKeyReleased(this::handleSearch);
        return searchField;
    }

    // Handle search event to filter the products based on user input
    private void handleSearch(KeyEvent event) {
        String query = ((TextField) event.getSource()).getText().toLowerCase();
        ObservableList<Product> filteredProducts = FXCollections.observableArrayList();

        for (Product product : productService.getAllProducts()) {
            if (product.getName().toLowerCase().contains(query) ||
                    productService.getCategoryNameById(product.getCategoryId()).toLowerCase().contains(query) ||
                    String.valueOf(product.getId()).contains(query)) {
                filteredProducts.add(product);
            }
        }

        productTable.setItems(filteredProducts);
    }

    // Add this method to your UI initialization to handle clicks outside the search field
    private void addClickOutsideHandler() {
        this.setOnMouseClicked(event -> {
            // Check if the click is not on the search field or the toggle button
            if (!searchField.isHover() && !searchField.isFocused() && !event.getTarget().equals(searchField)) {
                searchField.setVisible(false);
            }
        });
    }


    // Toggle visibility of the search field
    private void toggleSearchVisibility() {
        searchField.setVisible(!searchField.isVisible());
        if (searchField.isVisible()) searchField.requestFocus();
    }

    // Create the Edit column for the product table
    private TableColumn<Product, Void> createEditColumn() {
        TableColumn<Product, Void> editColumn = new TableColumn<>("Edit");
        editColumn.setCellFactory(param -> new TableCell<>() {
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
                        loadProductData();
                        return null;
                    };

                    EditProductDialog.show(new Stage(), connection, product, updateCallback);
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        });
        return editColumn;
    }

    // Create the Delete column for the product table
    private TableColumn<Product, Void> createDeleteColumn() {
        TableColumn<Product, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showDeleteConfirmationDialog(product);
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
        return deleteColumn;
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
        loadProductData(); // Refresh the product table after deletion
    }
}

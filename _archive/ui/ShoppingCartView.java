package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.model.Customer;
import com.joshuawilliams.ims.model.Employee;
import com.joshuawilliams.ims.model.Order;
import com.joshuawilliams.ims.model.Product;
import com.joshuawilliams.ims.model.ShoppingCart;
import com.joshuawilliams.ims.dao.ProductDao;
import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.service.CustomerService;
import com.joshuawilliams.ims.service.OrderService;
import com.joshuawilliams.ims.service.ProductService;
import com.joshuawilliams.ims.utils.SessionManager;

import java.sql.Connection;
import java.util.logging.Logger;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Cart / checkout screen for the ecommerce pivot.
 *
 * This is the one place in the app where the Product discount hierarchy is
 * demonstrated live, in front of the user: a cart holding a mix of
 * Electronics/Clothing/Groceries/GeneralProduct shows each line's own
 * discount, computed by whichever calculateDiscount() override actually
 * applies to that object at runtime — the same list of calls, different
 * behavior per item, resolved polymorphically.
 *
 * Follows the same construction pattern as OrdersView/ProductsView: plain
 * services passed in from MainApp, JavaFX controls built in private helper
 * methods, no FXML.
 */
public class ShoppingCartView extends BorderPane {

    private static final Logger logger = Logger.getLogger(ShoppingCartView.class.getName());

    private final CustomerService customerService;
    private final ProductService productService;
    private final OrderService orderService;
    private final Stage primaryStage;

    private final ComboBox<Customer> customerComboBox;
    private final ComboBox<Product> productComboBox;
    private final TextField quantityField;
    private final TableView<CartRow> cartTable;
    private final ObservableList<CartRow> cartRows = FXCollections.observableArrayList();

    private final Label subtotalValueLabel = new Label("GH¢0.00");
    private final Label discountValueLabel = new Label("GH¢0.00");
    private final Label totalValueLabel = new Label("GH¢0.00");
    private final Button checkoutButton = new Button("Checkout");

    // Null until a customer has been picked — a cart always belongs to
    // exactly one customer (ShoppingCart 1 -> 1 Customer).
    private ShoppingCart cart;

    public ShoppingCartView(CustomerService customerService, ProductService productService,
                            OrderService orderService, Stage primaryStage) {
        this.customerService = customerService;
        // MainApp's constructor has a known bug where its local ProductService
        // variable shadows the field, leaving this.productService null when
        // it's passed down. OrdersView already works around this the same
        // way — fall back to building a default instance rather than NPE.
        this.productService = (productService != null) ? productService : createDefaultProductService();
        this.orderService = orderService;
        this.primaryStage = primaryStage;

        this.customerComboBox = createCustomerComboBox();
        this.productComboBox = createProductComboBox();
        this.quantityField = createQuantityField();
        this.cartTable = createCartTable();
        cartTable.setItems(cartRows);

        totalValueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        checkoutButton.setOnAction(e -> handleCheckout());
        checkoutButton.setDisable(true);

        setTop(buildTopSection());
        setCenter(buildCenterSection());
        setBottom(buildBottomSection());
        setPadding(new Insets(10));

        refreshCartDisplay();
    }

    private ProductService createDefaultProductService() {
        logger.warning("ProductService was null when ShoppingCartView was constructed! Creating a default instance.");
        Connection connection = DatabaseConnection.getConnection();
        return new ProductService(new ProductDao(connection), connection);
    }

    // ----------------------------------------------------------------
    // Layout
    // ----------------------------------------------------------------

    private VBox buildTopSection() {
        Label titleLabel = new Label("Shopping Cart");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label customerLabel = new Label("Customer:");
        Label productLabel = new Label("Product:");
        Label quantityLabel = new Label("Quantity:");

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setOnAction(e -> handleAddToCart());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(customerLabel, 0, 0);
        form.add(customerComboBox, 1, 0);
        form.add(productLabel, 0, 1);
        form.add(productComboBox, 1, 1);
        form.add(quantityLabel, 2, 1);
        form.add(quantityField, 3, 1);
        form.add(addToCartButton, 4, 1);

        VBox top = new VBox(10, titleLabel, form);
        top.setPadding(new Insets(0, 0, 10, 0));
        return top;
    }

    private VBox buildCenterSection() {
        VBox center = new VBox(8, cartTable);
        VBox.setVgrow(cartTable, javafx.scene.layout.Priority.ALWAYS);
        return center;
    }

    private VBox buildBottomSection() {
        GridPane totals = new GridPane();
        totals.setHgap(10);
        totals.setVgap(4);
        totals.add(new Label("Subtotal:"), 0, 0);
        totals.add(subtotalValueLabel, 1, 0);
        totals.add(new Label("Discount:"), 0, 1);
        totals.add(discountValueLabel, 1, 1);
        totals.add(new Label("Total:"), 0, 2);
        totals.add(totalValueLabel, 1, 2);

        HBox totalsBox = new HBox(totals);
        totalsBox.setAlignment(Pos.CENTER_RIGHT);

        HBox actions = new HBox(10, checkoutButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox bottom = new VBox(10, totalsBox, actions);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        return bottom;
    }

    // ----------------------------------------------------------------
    // Customer selection
    // ----------------------------------------------------------------

    private ComboBox<Customer> createCustomerComboBox() {
        ComboBox<Customer> combo = new ComboBox<>(FXCollections.observableArrayList(customerService.getAllCustomers()));
        combo.setPromptText("Select a customer to start a cart");
        combo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer customer) {
                return customer != null ? customer.getFirstName() + " " + customer.getLastName() : "";
            }

            @Override
            public Customer fromString(String string) {
                return null;
            }
        });
        combo.setOnAction(e -> handleCustomerSelected(combo.getValue()));
        return combo;
    }

    private void handleCustomerSelected(Customer selected) {
        if (selected == null) {
            return;
        }
        boolean switchingAwayFromActiveCart = cart != null && !cart.isEmpty() && cart.getCustomer() != null
                && !cart.getCustomer().getCustomerId().equals(selected.getCustomerId());

        if (switchingAwayFromActiveCart) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Switch Customer");
            confirm.setHeaderText("This cart has items for " + cart.getCustomer().getFirstName() + ".");
            confirm.setContentText("Switching to a different customer will clear the current cart. Continue?");
            confirm.showAndWait().ifPresentOrElse(response -> {
                if (response == ButtonType.OK) {
                    startNewCart(selected);
                } else {
                    customerComboBox.setValue(cart.getCustomer());
                }
            }, () -> customerComboBox.setValue(cart.getCustomer()));
        } else {
            startNewCart(selected);
        }
    }

    private void startNewCart(Customer customer) {
        cart = new ShoppingCart(customer);
        refreshCartDisplay();
    }

    // ----------------------------------------------------------------
    // Product / quantity entry
    // ----------------------------------------------------------------

    private ComboBox<Product> createProductComboBox() {
        ComboBox<Product> combo = new ComboBox<>();
        combo.setPromptText("Select a product");
        combo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Product product) {
                return product != null ? product.getName() + " (" + product.getProductType() + ")" : "";
            }

            @Override
            public Product fromString(String string) {
                return null;
            }
        });
        combo.setOnMouseClicked(e -> {
            if (combo.getItems().isEmpty()) {
                loadProductsInto(combo);
            }
        });
        return combo;
    }

    private void loadProductsInto(ComboBox<Product> combo) {
        List<Product> products = productService.getAllProducts();
        combo.setItems(FXCollections.observableArrayList(products));
    }

    private TextField createQuantityField() {
        TextField field = new TextField();
        field.setPromptText("Qty");
        field.setPrefWidth(60);
        return field;
    }

    private void handleAddToCart() {
        if (cart == null) {
            showAlert(Alert.AlertType.WARNING, "No Customer Selected", "Please select a customer before adding items to the cart.");
            return;
        }
        Product selectedProduct = productComboBox.getValue();
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "No Product Selected", "Please select a product to add.");
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Quantity", "Please enter a valid whole number for quantity.");
            return;
        }

        try {
            cart.addItem(selectedProduct, quantity);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Cannot Add Item", e.getMessage());
            return;
        }

        quantityField.clear();
        refreshCartDisplay();
    }

    // ----------------------------------------------------------------
    // Cart table
    // ----------------------------------------------------------------

    private TableView<CartRow> createCartTable() {
        TableView<CartRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Cart is empty — select a customer and add products above."));

        TableColumn<CartRow, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));

        // The Type column matters here specifically: it's the visual proof
        // that the discount math below differs by runtime type, not by
        // hardcoded per-product logic.
        TableColumn<CartRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductType()));

        TableColumn<CartRow, Number> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()));

        TableColumn<CartRow, Number> unitPriceCol = new TableColumn<>("Unit Price");
        unitPriceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getUnitPrice()));

        TableColumn<CartRow, Number> discountCol = new TableColumn<>("Discount");
        discountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getLineDiscount()));

        TableColumn<CartRow, Number> lineTotalCol = new TableColumn<>("Line Total");
        lineTotalCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getLineTotal()));

        TableColumn<CartRow, Void> adjustCol = createAdjustColumn();
        TableColumn<CartRow, Void> removeCol = createRemoveColumn();

        table.getColumns().addAll(nameCol, typeCol, qtyCol, unitPriceCol, discountCol, lineTotalCol, adjustCol, removeCol);
        return table;
    }

    private TableColumn<CartRow, Void> createAdjustColumn() {
        TableColumn<CartRow, Void> column = new TableColumn<>("Adjust");
        column.setCellFactory(param -> new TableCell<>() {
            private final Button minusButton = new Button("-");
            private final Button plusButton = new Button("+");
            private final HBox box = new HBox(5, minusButton, plusButton);

            {
                box.setAlignment(Pos.CENTER);
                minusButton.setOnAction(e -> {
                    CartRow row = getTableView().getItems().get(getIndex());
                    adjustQuantity(row, -1);
                });
                plusButton.setOnAction(e -> {
                    CartRow row = getTableView().getItems().get(getIndex());
                    adjustQuantity(row, 1);
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        return column;
    }

    private void adjustQuantity(CartRow row, int delta) {
        int newQuantity = row.getQuantity() + delta;
        try {
            cart.updateItemQuantity(row.getProduct(), newQuantity);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Cannot Adjust Quantity", e.getMessage());
            return;
        }
        refreshCartDisplay();
    }

    private TableColumn<CartRow, Void> createRemoveColumn() {
        TableColumn<CartRow, Void> column = new TableColumn<>("Remove");
        column.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");

            {
                removeButton.setOnAction(e -> {
                    CartRow row = getTableView().getItems().get(getIndex());
                    cart.removeItem(row.getProduct());
                    refreshCartDisplay();
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeButton);
            }
        });
        return column;
    }

    // ----------------------------------------------------------------
    // Checkout
    // ----------------------------------------------------------------

    private void handleCheckout() {
        if (cart == null || cart.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cart Is Empty", "Add at least one product before checking out.");
            return;
        }

        Employee loggedInEmployee = SessionManager.getInstance().getLoggedInEmployee();
        String processedBy = loggedInEmployee != null ? loggedInEmployee.getName() : "Unknown";
        int processedById;
        try {
            processedById = loggedInEmployee != null ? Integer.parseInt(loggedInEmployee.getId()) : -1;
        } catch (NumberFormatException e) {
            processedById = -1;
        }

        Order order = cart.toOrder(processedBy, processedById);
        boolean success = orderService.createOrder(order);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Order Placed",
                    "Order submitted successfully. Total charged: GH¢" + String.format("%.2f", order.getTotalAmount()));
            cart.clear();
            // Stock quantities changed on checkout — reload the product list
            // so the combo box reflects current stock on the next add.
            loadProductsInto(productComboBox);
            refreshCartDisplay();
        } else {
            showAlert(Alert.AlertType.ERROR, "Checkout Failed",
                    "One or more items in the cart are no longer available in the requested quantity. Please review the cart and try again.");
        }
    }

    // ----------------------------------------------------------------
    // Shared refresh / helpers
    // ----------------------------------------------------------------

    private void refreshCartDisplay() {
        cartRows.clear();
        if (cart != null) {
            cart.getItems().forEach((product, quantity) -> cartRows.add(new CartRow(product, quantity)));
        }

        double subtotal = cart != null ? cart.calculateSubtotal() : 0.0;
        double discount = cart != null ? cart.calculateTotalDiscount() : 0.0;
        double total = cart != null ? cart.calculateTotal() : 0.0;

        subtotalValueLabel.setText("GH¢" + String.format("%.2f", subtotal));
        discountValueLabel.setText("GH¢" + String.format("%.2f", discount));
        totalValueLabel.setText("GH¢" + String.format("%.2f", total));

        checkoutButton.setDisable(cart == null || cart.isEmpty());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Read-only row wrapper so the cart's Map<Product, Integer> can be shown
     * in a TableView without changing ShoppingCart's own data shape. Every
     * getter that isn't a plain pass-through (getProductType, getLineDiscount,
     * getLineTotal) calls straight into the Product hierarchy's polymorphic
     * methods — this class deliberately does no discount math of its own.
     */
    private static class CartRow {
        private final Product product;
        private final int quantity;

        CartRow(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        Product getProduct() {
            return product;
        }

        String getProductName() {
            return product.getName();
        }

        String getProductType() {
            return product.getProductType();
        }

        int getQuantity() {
            return quantity;
        }

        double getUnitPrice() {
            return product.getPrice();
        }

        double getLineDiscount() {
            return product.calculateDiscount(quantity);
        }

        double getLineTotal() {
            return product.calculateLineTotal(quantity);
        }
    }
}
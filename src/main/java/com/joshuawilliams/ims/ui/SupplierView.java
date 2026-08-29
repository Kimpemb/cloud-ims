package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.controller.SupplierController;
import com.joshuawilliams.ims.model.Supplier;
import com.joshuawilliams.ims.model.SupplierProductRelation;
import com.joshuawilliams.ims.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SupplierView extends BorderPane {

    private final Stage primaryStage;
    private SupplierController controller;
    private TableView<Supplier> table;
    private TextField searchField;
    private ComboBox<String> categoryBox;
    private Pagination pagination;
    private static final int ITEMS_PER_PAGE = 8;

    public SupplierView(SupplierController controller, Stage primaryStage) {
        this.controller = controller;
        this.primaryStage = primaryStage;
        buildUI();
    }

    public void setController(SupplierController controller) {
        this.controller = controller;
    }

    private void buildUI() {
        setPadding(new Insets(10));

        // Top: Title, Search, Add, Refresh
        VBox top = new VBox(10);
        Label title = new Label("Supplier Management");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        searchField = new TextField();
        searchField.setPromptText("Search by name, email, or phone...");
        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> controller.onSearch(getSearchCriteria()));
        Button addBtn = new Button("Add New Supplier");
        addBtn.setOnAction(e -> controller.onAddSupplier());
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> controller.onRefresh());

        HBox searchBar = new HBox(10, new Label("Search:"), searchField, searchBtn);
        HBox topButtons = new HBox(10, addBtn, refreshBtn);
        topButtons.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        // Category Filter
        categoryBox = new ComboBox<>();
        categoryBox.getItems().add("All");
        categoryBox.getSelectionModel().selectFirst();

        HBox categoryFilter = new HBox(10, new Label("Category Filter:"), categoryBox);

        top.getChildren().addAll(title, searchBar, topButtons, categoryFilter);
        setTop(top);

        // Center: Table
        table = createSupplierTable();
        setCenter(table);

        // Bottom: Export, Reset Filters, Pagination
        Button exportBtn = new Button("Export Suppliers");
        exportBtn.setOnAction(e -> controller.onExport());
        Button resetBtn = new Button("Reset Filters");
        resetBtn.setOnAction(e -> resetFilters());

        pagination = new Pagination();
        pagination.setPageFactory(this::createPage);

        HBox bottom = new HBox(10, exportBtn, resetBtn, pagination);
        setBottom(bottom);
    }

    private TableView<Supplier> createSupplierTable() {
        TableView<Supplier> table = new TableView<>();
        table.setRowFactory(tv -> {
            TableRow<Supplier> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    controller.onEditSupplier();
                }
            });
            return row;
        });

        table.getColumns().addAll(
                createColumn("Supplier ID", "supplierId", 100),
                createColumn("Company Name", "supplierName", 150),
                createColumn("Contact Person", "supplierName", 150), // Using supplierName as no separate contact person field
                createColumn("Phone", "phoneNumber", 120),
                createColumn("Email", "emailAddress", 200),
                createColumn("Status", "status", 80),
                createActionsColumn()
        );

        return table;
    }

    private <T> TableColumn<Supplier, T> createColumn(String title, String property, int width) {
        TableColumn<Supplier, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);

        if ("status".equals(property)) {
            col.setCellFactory(column -> new TableCell<Supplier, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item.toString());
                        switch (item.toString()) {
                            case "Active" -> setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            case "Inactive" -> setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            case "Pending" -> setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            default -> setStyle("");
                        }
                    }
                }
            });
        }

        return col;
    }

    private TableColumn<Supplier, Void> createActionsColumn() {
        TableColumn<Supplier, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(120);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setOnAction(e -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    table.getSelectionModel().select(supplier);
                    controller.onEditSupplier();
                });
                deleteBtn.setOnAction(e -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    table.getSelectionModel().select(supplier);
                    controller.onDeleteSupplier();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
        return actionsCol;
    }

    private Node createPage(int pageIndex) {
        ObservableList<Supplier> suppliers = table.getItems();
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, suppliers.size());
        if (fromIndex < suppliers.size()) {
            table.setItems(FXCollections.observableArrayList(suppliers.subList(fromIndex, toIndex)));
        } else {
            table.setItems(FXCollections.observableArrayList());
        }
        return table;
    }

    private void resetFilters() {
        searchField.clear();
        categoryBox.getSelectionModel().selectFirst();
        controller.onRefresh();
    }

    // ----------------- Public API -----------------

    public void displaySuppliers(ObservableList<Supplier> suppliers) {
        table.setItems(suppliers);
        pagination.setPageCount((int) Math.ceil((double) suppliers.size() / ITEMS_PER_PAGE));
        pagination.setCurrentPageIndex(0);
        createPage(0); // Update table for first page
    }

    public void updateCategoryFilters(ObservableList<String> categories) {
        String selected = categoryBox.getValue();
        categoryBox.getItems().clear();
        categoryBox.getItems().add("All");
        categoryBox.getItems().addAll(categories);
        categoryBox.setValue(selected != null ? selected : "All");
    }

    public Supplier getSelectedSupplier() {
        return table.getSelectionModel().getSelectedItem();
    }

    public String getExportFormat() {
        return "CSV"; // Default to CSV as mockup mentions "Export Suppliers" without format selection
    }

    public Map<String, Object> getSearchCriteria() {
        Map<String, Object> criteria = new HashMap<>();
        if (!searchField.getText().isEmpty()) criteria.put("searchTerm", searchField.getText());
        if (!"All".equals(categoryBox.getValue())) criteria.put("category", categoryBox.getValue());
        return criteria;
    }

    public File showFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        return fileChooser.showSaveDialog(primaryStage);
    }

    public Optional<Supplier> showAddSupplierDialog() {
        return new SupplierDialog(primaryStage).showAndWait();
    }

    public Optional<Supplier> showEditSupplierDialog(Supplier supplier) {
        return new SupplierDialog(primaryStage, supplier).showAndWait();
    }

    public void showProductRelations(Supplier supplier, ObservableList<SupplierProductRelation> relations) {
        new ProductRelationDialog(primaryStage, supplier, relations).show();
    }

    public void showMetrics(Map<String, Object> metrics) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Supplier Performance Metrics");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label title = new Label("Performance Summary");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;
        for (Map.Entry<String, Object> entry : metrics.entrySet()) {
            Label keyLabel = new Label(entry.getKey() + ":");
            keyLabel.setStyle("-fx-font-weight: bold;");
            Label valueLabel = new Label(entry.getValue().toString());
            grid.addRow(row++, keyLabel, valueLabel);
        }

        content.getChildren().addAll(title, new Separator(), grid);

        Scene scene = new Scene(new ScrollPane(content), 400, 300);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void showSuccess(String message) {
        AlertHelper.showInformationDialog("Success", message);
    }

    public void showError(String title, String message) {
        AlertHelper.showErrorDialog(title, message);
    }

    public boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    // ----------------- ProductRelationDialog -----------------

    public static class ProductRelationDialog {
        private final Stage stage;

        public ProductRelationDialog(Stage owner, Supplier supplier, ObservableList<SupplierProductRelation> relations) {
            stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Products for Supplier: " + supplier.getSupplierName());

            TableView<SupplierProductRelation> table = new TableView<>(relations);

            TableColumn<SupplierProductRelation, String> productCol = new TableColumn<>("Product");
            productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

            TableColumn<SupplierProductRelation, Integer> quantityCol = new TableColumn<>("Quantity Supplied");
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantitySupplied"));

            TableColumn<SupplierProductRelation, String> lastDateCol = new TableColumn<>("Last Supplied");
            lastDateCol.setCellValueFactory(new PropertyValueFactory<>("lastSuppliedDate"));

            table.getColumns().addAll(productCol, quantityCol, lastDateCol);

            Scene scene = new Scene(new VBox(table), 600, 400);
            stage.setScene(scene);
        }

        public void show() {
            stage.showAndWait();
        }
    }

    // ----------------- SupplierDialog -----------------

    private static class SupplierDialog extends Dialog<Supplier> {

        private final TextField nameField = new TextField();
        private final TextField contactPersonField = new TextField();
        private final TextField phoneField = new TextField();
        private final TextField emailField = new TextField();
        private final ComboBox<String> statusBox = new ComboBox<>();

        public SupplierDialog(Stage owner) {
            this(owner, null);
        }

        public SupplierDialog(Stage owner, Supplier supplier) {
            initOwner(owner);
            initModality(Modality.APPLICATION_MODAL);
            setTitle(supplier == null ? "Add Supplier" : "Edit Supplier");

            statusBox.getItems().addAll("Active", "Inactive", "Pending");
            statusBox.getSelectionModel().selectFirst();

            if (supplier != null) {
                nameField.setText(supplier.getSupplierName());
                contactPersonField.setText(supplier.getSupplierName()); // Using supplierName for Contact Person
                phoneField.setText(supplier.getPhoneNumber());
                emailField.setText(supplier.getEmailAddress());
                statusBox.setValue(supplier.getStatus());
            }

            GridPane grid = new GridPane();
            grid.setVgap(10);
            grid.setHgap(10);
            grid.setPadding(new Insets(10));

            grid.addRow(0, new Label("Company Name:"), nameField);
            grid.addRow(1, new Label("Contact Person:"), contactPersonField);
            grid.addRow(2, new Label("Phone:"), phoneField);
            grid.addRow(3, new Label("Email:"), emailField);
            grid.addRow(4, new Label("Status:"), statusBox);

            getDialogPane().setContent(grid);
            getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    return new Supplier(
                            nameField.getText(),
                            emailField.getText(),
                            phoneField.getText(),
                            null, // Category not used in dialog per mockup
                            statusBox.getValue(),
                            supplier != null ? supplier.getSupplierId() : null,
                            0 // Reliability not used in dialog
                    );
                }
                return null;
            });
        }
    }
}
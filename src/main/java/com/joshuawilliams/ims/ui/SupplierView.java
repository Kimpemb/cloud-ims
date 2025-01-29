package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.model.Supplier;
import com.joshuawilliams.ims.service.SupplierService;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;

public class SupplierView extends VBox {

    private final SupplierService supplierService;
    private TableView<Supplier> supplierTable;
    private TextField searchField;
    private Button addButton;
    private Button refreshButton;

    public SupplierView(SupplierService supplierService) {
        this.supplierService = supplierService;
        createUI();
    }

    private void createUI() {
        setPadding(new Insets(10));

        // Search bar (initially hidden)
        searchField = new TextField();
        searchField.setPromptText("Search Suppliers...");
        searchField.setVisible(false); // Start with it hidden
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterSuppliers(newValue));

        // TableView for suppliers
        supplierTable = new TableView<>();
        supplierTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Define columns
        TableColumn<Supplier, String> supplierIdColumn = new TableColumn<>("Supplier ID");
        supplierIdColumn.setCellValueFactory(param -> param.getValue().supplierIdProperty());

        TableColumn<Supplier, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(param -> param.getValue().supplierNameProperty());

        TableColumn<Supplier, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(param -> param.getValue().emailAddressProperty());

        TableColumn<Supplier, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(param -> param.getValue().phoneNumberProperty());

        TableColumn<Supplier, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(param -> param.getValue().addressProperty());

        TableColumn<Supplier, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(param -> param.getValue().categoryProperty());

        TableColumn<Supplier, String> paymentTermsColumn = new TableColumn<>("Payment Terms");
        paymentTermsColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPaymentTerms()));

        TableColumn<Supplier, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(param -> param.getValue().statusProperty());

        TableColumn<Supplier, String> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(getActionCellFactory());

        supplierTable.getColumns().addAll(supplierIdColumn, nameColumn, emailColumn, phoneColumn, addressColumn, categoryColumn, paymentTermsColumn, statusColumn, actionColumn);

        // Load suppliers
        loadSuppliers();

        // Add Supplier button
        addButton = new Button("Add Supplier");
        addButton.setOnAction(e -> showSupplierDialog(null));

        // Refresh button
        refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadSuppliers());

        // Search button (magnifying glass)
        Button searchButton = new Button("\uD83D\uDD0D"); // Unicode magnifying glass
        searchButton.setStyle("-fx-font-size: 14px;");
        searchButton.setOnAction(e -> toggleSearchFieldVisibility());

        // Layout for buttons
        HBox buttonBar = new HBox(10, addButton, searchButton, refreshButton);
        buttonBar.setPadding(new Insets(10));
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        // Wrap the button bar and search field in a VBox
        VBox contentBox = new VBox(buttonBar, searchField, supplierTable);

        // TabPane for the view
        TabPane tabPane = new TabPane();
        Tab supplierTab = new Tab("Suppliers");
        supplierTab.setClosable(false);
        supplierTab.setContent(contentBox);

        tabPane.getTabs().add(supplierTab);

        getChildren().add(tabPane);

        // Set click listener to hide search field when clicked outside
        setOnMouseClicked(event -> {
            if (!searchField.isVisible() || event.getTarget() instanceof TextField) return;
            searchField.setVisible(false);
        });
    }

    // Toggle visibility of the search field
    private void toggleSearchFieldVisibility() {
        searchField.setVisible(!searchField.isVisible());
    }


    private Callback<TableColumn<Supplier, String>, TableCell<Supplier, String>> getActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> showSupplierDialog(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> deleteSupplier(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        };
    }

    private void loadSuppliers() {
        supplierTable.getItems().clear();
        supplierTable.getItems().addAll(supplierService.getAllSuppliers());
    }

    private void filterSuppliers(String query) {
        if (query == null || query.isEmpty()) {
            loadSuppliers();
        } else {
            supplierTable.getItems().clear();
            supplierTable.getItems().addAll(supplierService.getAllSuppliers().stream()
                    .filter(supplier -> supplier.getSupplierName().toLowerCase().contains(query.toLowerCase()) ||
                            supplier.getEmailAddress().toLowerCase().contains(query.toLowerCase()))
                    .toList());
        }
    }

    private void showSupplierDialog(Supplier supplier) {
        Dialog<Supplier> dialog = new Dialog<>();
        dialog.setTitle(supplier == null ? "Add Supplier" : "Edit Supplier");
        dialog.setHeaderText(supplier == null ? "Enter Supplier Details" : "Edit Supplier Details");

        // Form fields
        TextField nameField = new TextField(supplier != null ? supplier.getSupplierName() : "");
        nameField.setPromptText("Supplier Name");

        TextField emailField = new TextField(supplier != null ? supplier.getEmailAddress() : "");
        emailField.setPromptText("Supplier Email");

        TextField phoneField = new TextField(supplier != null ? supplier.getPhoneNumber() : "");
        phoneField.setPromptText("Phone Number");

        TextField addressField = new TextField(supplier != null ? supplier.getAddress() : "");
        addressField.setPromptText("Address");

        TextField websiteField = new TextField(supplier != null ? supplier.getWebsiteUrl() : "");
        websiteField.setPromptText("Website URL");

        TextField categoryField = new TextField(supplier != null ? supplier.getCategory() : "");
        categoryField.setPromptText("Category");

        TextField bankAccountField = new TextField(supplier != null ? supplier.getBankAccountDetails() : "");
        bankAccountField.setPromptText("Bank Account Details");

        TextField paymentTermsField = new TextField(supplier != null ? supplier.getPaymentTerms() : "");
        paymentTermsField.setPromptText("Payment Terms");

        TextField statusField = new TextField(supplier != null ? supplier.getStatus() : "Active");
        statusField.setPromptText("Status");

        TextField notesField = new TextField(supplier != null ? supplier.getNotes() : "");
        notesField.setPromptText("Notes");

        VBox content = new VBox(10, nameField, emailField, phoneField, addressField, websiteField, categoryField, bankAccountField, paymentTermsField, statusField, notesField);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Set result converter
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                return new Supplier(
                        supplier == null ? "" : supplier.getSupplierId(),
                        nameField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        addressField.getText(),
                        websiteField.getText(),
                        categoryField.getText(),
                        bankAccountField.getText(),
                        paymentTermsField.getText(),
                        0,
                        0,
                        statusField.getText(),
                        notesField.getText()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(resultSupplier -> {
            if (supplier == null) {
                resultSupplier.setSupplierId(resultSupplier.generateSupplierId(supplierService.getSupplierCount()));
                supplierService.addSupplier(resultSupplier);
                showSuccessMessage("Supplier added successfully!");
            } else {
                supplierService.updateSupplier(resultSupplier);
                showSuccessMessage("Supplier updated successfully!");
            }
            loadSuppliers();
        });
    }

    private void deleteSupplier(Supplier supplier) {
        supplierService.deleteSupplier(supplier.getSupplierId());
        loadSuppliers();
        showSuccessMessage("Supplier deleted successfully!");
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

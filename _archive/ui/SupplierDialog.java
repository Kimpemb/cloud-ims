package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.model.Supplier;
import com.joshuawilliams.ims.utils.AlertHelper;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.UUID;

public class SupplierDialog {

    private final Stage dialogStage;

    // Input fields
    private final TextField idField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField addressField = new TextField();
    private final TextField websiteField = new TextField();
    private final ComboBox<String> categoryCombo = new ComboBox<>();
    private final TextField bankField = new TextField();
    private final TextField termsField = new TextField();
    private final Spinner<Integer> reliabilitySpinner = new Spinner<>();
    private final Spinner<Integer> deliverySpinner = new Spinner<>();
    private final TextArea notesArea = new TextArea();

    private Supplier result;

    private static final String[] CATEGORIES = {
            "Electronics", "Hardware", "Office Supplies", "Raw Materials"
    };

    public SupplierDialog(Stage owner) {
        this.dialogStage = new Stage();
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
    }

    public Supplier showAddSupplierDialog() {
        return showDialog(null, "Add New Supplier");
    }

    public Supplier showEditSupplierDialog(Supplier supplier) {
        return showDialog(supplier, "Edit Supplier");
    }

    private Supplier showDialog(Supplier supplier, String title) {
        Dialog<Supplier> dialog = new Dialog<>();
        dialog.initOwner(dialogStage);
        dialog.setTitle(title);
        dialog.getDialogPane().setMinWidth(500);

        ButtonType confirmButtonType = new ButtonType(supplier == null ? "Add" : "Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane formGrid = createFormGrid();
        populateForm(supplier);

        ScrollPane scrollPane = new ScrollPane(formGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(Region.USE_COMPUTED_SIZE);
        dialog.getDialogPane().setContent(scrollPane);

        // Disable confirm button if required fields are missing
        Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        Runnable validateForm = () -> confirmButton.setDisable(!isFormValid());
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm.run());
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> validateForm.run());
        categoryCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateForm.run());

        dialog.setResultConverter(button -> {
            if (button == confirmButtonType && isFormValid()) {
                return buildSupplierFromForm();
            }
            return null;
        });

        Optional<Supplier> optionalResult = dialog.showAndWait();
        result = optionalResult.orElse(null);
        return result;
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        SpinnerValueFactory<Integer> reliabilityFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5, 3);
        SpinnerValueFactory<Integer> deliveryFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5, 3);
        reliabilitySpinner.setValueFactory(reliabilityFactory);
        deliverySpinner.setValueFactory(deliveryFactory);

        categoryCombo.getItems().setAll(CATEGORIES);
        categoryCombo.setPromptText("Select a category");

        idField.setDisable(true);
        notesArea.setPrefRowCount(3);
        notesArea.setPromptText("Add any relevant notes...");
        nameField.setPromptText("Supplier name");
        emailField.setPromptText("example@example.com");
        phoneField.setPromptText("e.g. +233 123 456 789");
        websiteField.setPromptText("https://supplier.com");
        termsField.setPromptText("e.g. Net 30, COD");

        int row = 0;
        grid.add(new Label("ID:"), 0, row);
        grid.add(idField, 1, row++);

        grid.add(new Label("Name*:"), 0, row);
        grid.add(nameField, 1, row++);

        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);

        grid.add(new Label("Phone*:"), 0, row);
        grid.add(phoneField, 1, row++);

        grid.add(new Label("Address:"), 0, row);
        grid.add(addressField, 1, row++);

        grid.add(new Label("Website:"), 0, row);
        grid.add(websiteField, 1, row++);

        grid.add(new Label("Category*:"), 0, row);
        grid.add(categoryCombo, 1, row++);

        grid.add(new Label("Bank Details:"), 0, row);
        grid.add(bankField, 1, row++);

        grid.add(new Label("Payment Terms:"), 0, row);
        grid.add(termsField, 1, row++);

        grid.add(new Label("Reliability (0–5):"), 0, row);
        grid.add(reliabilitySpinner, 1, row++);

        grid.add(new Label("Delivery (0–5):"), 0, row);
        grid.add(deliverySpinner, 1, row++);

        grid.add(new Label("Notes:"), 0, row);
        grid.add(notesArea, 1, row);

        // Make all input fields grow horizontally
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(emailField, Priority.ALWAYS);
        GridPane.setHgrow(phoneField, Priority.ALWAYS);
        GridPane.setHgrow(addressField, Priority.ALWAYS);
        GridPane.setHgrow(websiteField, Priority.ALWAYS);
        GridPane.setHgrow(bankField, Priority.ALWAYS);
        GridPane.setHgrow(termsField, Priority.ALWAYS);
        GridPane.setHgrow(notesArea, Priority.ALWAYS);

        return grid;
    }

    private void populateForm(Supplier supplier) {
        if (supplier != null) {
            idField.setText(supplier.getSupplierId());
            nameField.setText(supplier.getSupplierName());
            emailField.setText(supplier.getEmailAddress());
            phoneField.setText(supplier.getPhoneNumber());
            addressField.setText(supplier.getAddress());
            websiteField.setText(supplier.getWebsiteUrl());
            categoryCombo.setValue(supplier.getCategory());
            bankField.setText(supplier.getBankAccountDetails());
            termsField.setText(supplier.getPaymentTerms());

            reliabilitySpinner.getValueFactory().setValue(supplier.getReliabilityRating());
            deliverySpinner.getValueFactory().setValue(supplier.getDeliveryPerformance());

            notesArea.setText(supplier.getNotes());
        } else {
            idField.setText(generateUniqueSupplierId());
        }
    }

    private boolean isFormValid() {
        return !nameField.getText().trim().isEmpty()
                && !phoneField.getText().trim().isEmpty()
                && categoryCombo.getValue() != null
                && !categoryCombo.getValue().trim().isEmpty();
    }

    private Supplier buildSupplierFromForm() {
        return new Supplier(
                idField.getText().trim(),
                nameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                addressField.getText().trim(),
                websiteField.getText().trim(),
                categoryCombo.getValue(),
                bankField.getText().trim(),
                termsField.getText().trim(),
                reliabilitySpinner.getValue(),
                deliverySpinner.getValue(),
                "Active",
                notesArea.getText().trim()
        );
    }

    private String generateUniqueSupplierId() {
        return "SUP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

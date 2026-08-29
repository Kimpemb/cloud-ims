package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.controller.SupplierController;
import com.joshuawilliams.ims.model.Supplier;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

public class SupplierTableCell extends TableCell<Supplier, Void> {

    private final HBox container = new HBox(5);
    private final Button editButton = new Button("Edit");
    private final Button deleteButton = new Button("Delete");
    private final SupplierController controller;

    public SupplierTableCell(SupplierController controller) {
        this.controller = controller;
        setupButtons();
        container.getChildren().addAll(editButton, deleteButton);
    }

    private void setupButtons() {
        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        editButton.setOnAction(event -> {
            Supplier supplier = getTableRow().getItem();
            if (supplier != null) {
                controller.onEditSupplier(); // Changed from handleEditSupplier
            }
        });

        deleteButton.setOnAction(event -> {
            Supplier supplier = getTableRow().getItem();
            if (supplier != null) {
                controller.onDeleteSupplier();  // Matches the controller method name
            }
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(container);
        }
    }

    public static void createActionColumn(TableColumn<Supplier, Void> column, SupplierController controller) {
        column.setCellFactory(param -> new SupplierTableCell(controller));
    }
}
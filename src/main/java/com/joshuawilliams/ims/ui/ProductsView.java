package com.joshuawilliams.ims.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.sql.Connection;

public class ProductsView extends StackPane {

    public ProductsView(Connection connection, MainApp mainApp) {
        // Create a button for adding a new product
        Button addProductButton = new Button("Add New Product");

        // Action to open the AddProductDialog when clicked
        addProductButton.setOnAction(e ->
                AddProductDialog.show(new Stage(), connection, mainApp::insertProduct) // Pass the callback method from mainApp
        );

        // Add the button to the layout
        this.getChildren().add(addProductButton);
    }
}

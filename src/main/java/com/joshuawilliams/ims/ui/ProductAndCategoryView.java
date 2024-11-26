package com.joshuawilliams.ims.ui;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;

public class ProductAndCategoryView extends VBox {
    private TabPane tabPane;

    public ProductAndCategoryView(Connection connection, MainApp mainApp) {
        tabPane = new TabPane();

        // Tab for Product Management
        Tab productTab = new Tab("Product Management");
        productTab.setContent(new ProductsView(connection, mainApp)); // Your existing Product view

        // Tab for Category Management
        Tab categoryTab = new Tab("Category Management");
        categoryTab.setContent(new CategoryView(connection, mainApp)); // Your existing Category view

        tabPane.getTabs().addAll(productTab, categoryTab);

        // Ensure the TabPane takes the full height/width of the parent container
        this.setSpacing(10);
        this.getChildren().add(tabPane);
    }

}

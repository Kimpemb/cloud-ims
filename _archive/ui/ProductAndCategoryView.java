package com.joshuawilliams.ims.ui;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;

public class ProductAndCategoryView extends VBox {
    private final TabPane tabPane;
    private Tab categoryManagementTab;  // Declare this before using it

    public ProductAndCategoryView(Connection connection, MainApp mainApp) {
        // Create the TabPane
        tabPane = new TabPane();

        // Tab for Category Management
        categoryManagementTab = new Tab("Category Management");
        categoryManagementTab.setClosable(false);
        categoryManagementTab.setContent(new CategoryView(connection, mainApp));

        // Tab for Product Management
        Tab productTab = new Tab("Product Management");
        productTab.setClosable(false);
        productTab.setContent(new ProductsView(connection, mainApp, tabPane, categoryManagementTab));

        tabPane.getTabs().addAll(productTab, categoryManagementTab);

        // Add TabPane to this view
        this.getChildren().add(tabPane);
    }

    // Getter for TabPane
    public TabPane getTabPane() {
        return tabPane;
    }

    // Getter for Category Management Tab
    public Tab getCategoryManagementTab() {
        return categoryManagementTab;
    }

    public Tab getCategoryTab() {
        return tabPane.getTabs().stream()
                .filter(tab -> "Category Management".equals(tab.getText()))
                .findFirst()
                .orElse(null);
    }
}

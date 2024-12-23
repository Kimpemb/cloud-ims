package com.joshuawilliams.ims.ui;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;

public class ProductAndCategoryView extends VBox {
    private final TabPane tabPane;
    private Tab categoryManagementTab;  // Declare this before using it

    public ProductAndCategoryView(Connection connection, MainApp mainApp) {
        // Initialize TabPane
        tabPane = new TabPane();

        // Tab for Category Management
        categoryManagementTab = new Tab("Category Management");  // Initialize here
        categoryManagementTab.setClosable(false); // Prevent users from closing the tab
        categoryManagementTab.setContent(new CategoryView(connection, mainApp)); // Your existing Category view

        // Tab for Product Management
        Tab productTab = new Tab("Product Management");
        productTab.setClosable(false); // Prevent users from closing the tab
        productTab.setContent(new ProductsView(connection, mainApp, tabPane, categoryManagementTab)); // Pass the initialized categoryManagementTab

        // Add tabs to TabPane
        tabPane.getTabs().addAll(productTab, categoryManagementTab);

        // Set the default active tab to Product Management
        tabPane.getSelectionModel().select(productTab);

        // Ensure the TabPane takes the full height/width of the parent container
        this.setSpacing(10); // Optional spacing between components
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

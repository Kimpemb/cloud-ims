package com.joshuawilliams.ims.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class InventoryView extends StackPane {

    public InventoryView() {
        this.getChildren().add(new Label("Welcome to the Inventory Section"));
    }
}

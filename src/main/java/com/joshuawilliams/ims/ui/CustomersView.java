package com.joshuawilliams.ims.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class CustomersView extends StackPane {

    public CustomersView() {
        this.getChildren().add(new Label("Welcome to the Customers Section"));
    }
}

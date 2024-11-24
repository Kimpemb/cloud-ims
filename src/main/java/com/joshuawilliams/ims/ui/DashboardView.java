package com.joshuawilliams.ims.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;

public class DashboardView extends StackPane {
    public DashboardView() {
        // Add components to the view
        Label label = new Label("Dashboard View");
        this.getChildren().add(label);
    }
}

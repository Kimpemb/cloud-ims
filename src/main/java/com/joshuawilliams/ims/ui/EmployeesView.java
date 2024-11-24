package com.joshuawilliams.ims.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;

public class EmployeesView extends StackPane {
    public EmployeesView() {
        Label label = new Label("Employees View Content");
        getChildren().add(label);
    }
}

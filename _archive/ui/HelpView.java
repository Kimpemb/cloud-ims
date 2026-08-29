package com.joshuawilliams.ims.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class HelpView extends StackPane {

    public HelpView() {
        this.getChildren().add(new Label("Welcome to the Help Section"));
    }
}

package com.joshuawilliams.ims.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class SettingsView extends StackPane {

    public SettingsView() {
        this.getChildren().add(new Label("Welcome to the Settings Section"));
    }
}

package com.joshuawilliams.ims.ui;


import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class NotificationsView extends VBox {

    public NotificationsView() {
        // Adding a simple label for now, you can later expand with dynamic data
        Label notificationLabel = new Label("No new notifications at the moment.");
        this.getChildren().add(notificationLabel);
    }
}


package com.joshuawilliams.ims.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WelcomeView {
    public void showWelcomeScreen(Stage primaryStage) {
        Label welcomeMessage = new Label("Welcome to the Inventory Management System!");
        Label credentialsInfo = new Label(
                "A default admin account has been created:\n\n" +
                        "Email: admin@system.com\n" +
                        "Password: Admin@1234\n\n" +
                        "For security reasons, you will be required to change this password immediately upon login."
        );

        Button proceedButton = new Button("Proceed to Login");
        proceedButton.setOnAction(event -> new LoginView().showLoginScreen(primaryStage));

        VBox layout = new VBox(10, welcomeMessage, credentialsInfo, proceedButton);
        layout.setPadding(new Insets(20));
        Scene scene = new Scene(layout, 400, 200);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Welcome");
        primaryStage.show();
    }
}


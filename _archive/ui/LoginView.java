package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.dao.EmployeeDao;
import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.model.Employee;
import com.joshuawilliams.ims.service.EmployeeService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.Optional;

public class LoginView {

    // Initialize EmployeeService with a shared connection and DAO
    private final Connection connection = DatabaseConnection.getConnection();
    private final EmployeeDao employeeDao = new EmployeeDao(connection);
    private final EmployeeService employeeService = new EmployeeService(employeeDao, connection);

    public void showLoginScreen(Stage stage) {
        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin(stage, emailField.getText().trim(), passwordField.getText().trim(), errorLabel));

        VBox layout = new VBox(10, titleLabel, emailField, passwordField, loginButton, errorLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Inventory Management System - Login");
        stage.show();
    }

    private void handleLogin(Stage stage, String email, String password, Label errorLabel) {
        try {
            Optional<Employee> employeeOptional = employeeService.login(email, password);
            if (employeeOptional.isPresent()) {
                if (employeeService.isDefaultAdminPassword(email)) {
                    // Force password change for default admin
                    new ForcePasswordChangeView().show(stage, email);
                } else {
                    MainApp.getInstance().loadAppView(email);
                }
            } else {
                errorLabel.setText("Login failed. Please try again.");
            }
        } catch (Exception ex) {
            errorLabel.setText("An unexpected error occurred.");
            ex.printStackTrace();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle(title);
        alert.showAndWait();
    }
}

// ForcePasswordChangeView.java
package com.joshuawilliams.ims.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.joshuawilliams.ims.service.EmployeeService;
import com.joshuawilliams.ims.dao.EmployeeDao;
import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.utils.PasswordUtils;
import com.joshuawilliams.ims.ui.MainApp;

import java.sql.Connection;

public class ForcePasswordChangeView {

    private final Connection connection = DatabaseConnection.getConnection();
    private final EmployeeDao employeeDao = new EmployeeDao(connection);
    private final EmployeeService employeeService = new EmployeeService(employeeDao, connection);

    public void show(Stage stage, String email) {
        Label titleLabel = new Label("Change Default Admin Password");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label emailLabel = new Label("Email: " + email);

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> handleChangePassword(stage, email, newPasswordField.getText().trim(), confirmPasswordField.getText().trim()));

        VBox layout = new VBox(10, titleLabel, emailLabel, newPasswordField, confirmPasswordField, changePasswordButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Force Password Change");
        stage.show();
    }

    private void handleChangePassword(Stage stage, String email, String newPassword, String confirmPassword) {
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Input Error", "All fields are required.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Password Mismatch", "Passwords do not match.");
            return;
        }

        if (!employeeService.isValidPassword(newPassword)) {
            showError("Weak Password", "Password must contain at least 8 characters, including uppercase, lowercase, digit, and special character.");
            return;
        }

        try {
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            boolean success = employeeService.updatePasswordByEmail(email, hashedPassword);

            if (success) {
                showInfo("Success", "Password changed successfully!");
                MainApp.getInstance().loadAppView(email);
            } else {
                showError("Update Failed", "Failed to update the password. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle(title);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle(title);
        alert.showAndWait();
    }
}

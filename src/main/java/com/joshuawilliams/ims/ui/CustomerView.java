package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.controller.CustomerController;
import com.joshuawilliams.ims.model.Customer;
import com.joshuawilliams.ims.model.Status;
import com.joshuawilliams.ims.dao.CustomerDao;
import com.joshuawilliams.ims.service.CustomerService;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.sql.Connection;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class CustomerView extends StackPane {
    private final CustomerController customerController;
    private TextField firstNameField, lastNameField, emailField, phoneField, addressField, dobField, notesField;
    private ComboBox<String> statusComboBox, loyaltyLevelComboBox;
    private Button createButton, cancelButton;

    private VBox dialogLayout;  // To store the dialog content for easy hiding/showing
    private Stage dialogStage;   // To manage the dialog window

    public CustomerView(Connection connection) {
        // Initialize CustomerDao with the provided connection
        CustomerDao customerDao = new CustomerDao(connection);

        // Initialize CustomerService with the CustomerDao
        CustomerService customerService = new CustomerService(customerDao);

        // Initialize CustomerController with the CustomerService
        customerController = new CustomerController(customerService);

        // Initialize UI
        initializeUI();
    }

    private void initializeUI() {
        // Create Tabs
        Tab manageCustomersTab = new Tab("Manage Customers");
        manageCustomersTab.setContent(createManageCustomersUI());

        // Set up TabPane and add the "Manage Customers" tab
        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(manageCustomersTab);

        // Set TabPane as the content of this view
        this.getChildren().add(tabPane);
    }

    private VBox createManageCustomersUI() {
        VBox layout = new VBox(20);

        // Button to trigger Add Customer Dialog
        Button addCustomerButton = new Button("Add New Customer");
        addCustomerButton.setOnAction(e -> openAddCustomerDialog());
        layout.getChildren().add(addCustomerButton);

        return layout;
    }

    private void openAddCustomerDialog() {
        // Create and show the dialog if not already created
        if (dialogStage == null) {
            createAddCustomerDialog();
        }

        dialogStage.showAndWait();
    }



    private void createAddCustomerDialog() {
        // Initialize UI elements for the dialog
        dialogLayout = new VBox(10);

        firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        emailField = new TextField();
        emailField.setPromptText("Email");

        phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        addressField = new TextField();
        addressField.setPromptText("Address");

        dobField = new TextField();
        dobField.setPromptText("Date of Birth (YYYY-MM-DD)");

        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Active", "Inactive");
        statusComboBox.setPromptText("Select Status");

        loyaltyLevelComboBox = new ComboBox<>();
        loyaltyLevelComboBox.getItems().addAll("Bronze", "Silver", "Gold");
        loyaltyLevelComboBox.setPromptText("Select Loyalty Level");

        notesField = new TextField();
        notesField.setPromptText("Notes");

        createButton = new Button("Create Customer");
        createButton.setOnAction(event -> createCustomer());

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> closeAddCustomerDialog());

        // Add all the fields and the buttons to the dialog layout
        dialogLayout.getChildren().addAll(
                firstNameField, lastNameField, emailField, phoneField,
                addressField, dobField, statusComboBox, loyaltyLevelComboBox,
                notesField, createButton, cancelButton
        );

        // Add padding to the dialog layout
        dialogLayout.setPadding(new Insets(20));

        // Wrap the dialogLayout with a ScrollPane to allow scrolling if content exceeds window size
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(dialogLayout);
        scrollPane.setFitToWidth(true);  // Ensure the content fits the width of the window

        // Create the dialog window
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Make it modal to block interaction with the main window
        dialogStage.initOwner(this.getScene().getWindow());  // Make the dialog owner the main app window

        // Set dialog size (You can adjust these values as needed)
        dialogStage.setWidth(500);   // Set dialog width
        dialogStage.setHeight(400);  // Set dialog height

        // Prevent maximizing the dialog
        dialogStage.setResizable(false);

        // Set the content of the dialog to be the scrollable view
        dialogStage.setTitle("Add Customer");
        dialogStage.setScene(new javafx.scene.Scene(scrollPane));
    }



    private void closeAddCustomerDialog() {
        // Close the dialog when cancel or after creation
        dialogStage.close();
    }

    private void createCustomer() {
        // Extract data from fields
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneField.getText();
        String address = addressField.getText();
        String dobString = dobField.getText();
        String statusString = statusComboBox.getValue();
        String loyaltyLevel = loyaltyLevelComboBox.getValue();
        String notes = notesField.getText().trim(); // Trim any leading/trailing spaces

        // Default loyalty points (could be modified based on loyalty level)
        int loyaltyPoints = 0;

        // Convert date of birth from String to LocalDate
        LocalDate dob = LocalDate.parse(dobString);

        // Convert status string to Status enum
        Status status = Status.valueOf(statusString.toUpperCase());

        // Create customer object
        Customer customer = new Customer(
                "", firstName, lastName, email, phoneNumber, address, dob,
                status, LocalDate.now(), loyaltyPoints, notes, loyaltyLevel
        );

        // Call the controller to create the customer
        boolean success = customerController.createCustomer(customer);

        // Show success or failure message
        if (success) {
            showAlert(AlertType.INFORMATION, "Customer Created", "Customer Created Successfully",
                    "Customer ID: " + customer.getCustomerId() + "\nName: " + customer.getFirstName() + " " + customer.getLastName());
            closeAddCustomerDialog();  // Close the dialog after customer creation
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to Create Customer", "Please try again.");
        }
    }

    private void showAlert(AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}

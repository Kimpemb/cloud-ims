package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.controller.CustomerController;
import com.joshuawilliams.ims.model.Customer;
import com.joshuawilliams.ims.model.Status;
import com.joshuawilliams.ims.dao.CustomerDao;
import com.joshuawilliams.ims.service.CustomerService;

import java.util.ArrayList;
import java.util.Optional;  // Add this import at the top of your class

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.sql.Connection;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.util.List;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;



public class CustomerView extends StackPane {
    private final CustomerService customerService;
    private final CustomerController customerController;
    private static final Logger logger = Logger.getLogger(CustomerView.class.getName());
    private TableView<Customer> customerTable;
    private TextField firstNameField, lastNameField, emailField, phoneField, addressField, dobField;
    private TextField loyaltyLevelField; // Renamed from notesField
    private ComboBox<String> statusComboBox;
    private Button createButton, cancelButton;
    private ComboBox<String> loyaltyLevelComboBox;
    private TextField notesField; // Declare the TextField for Notes
    private DatePicker dobPicker; // Declare at the class level
    private VBox dialogLayout;  // To store the dialog content for easy hiding/showing
    private Stage dialogStage;   // To manage the dialog window

    public CustomerView(CustomerService customerService) {
        // Initialize CustomerService with the provided service
        this.customerService = customerService;

        // Initialize CustomerController with the CustomerService
        this.customerController = new CustomerController(customerService);  // Initialize the controller here

        // Initialize customerTable
        this.customerTable = createCustomerTable();  // Initialize customerTable in the constructor

        // Initialize UI
        initializeUI();
    }



    private void initializeUI() {
        // Create the "Manage Customers" tab
        Tab manageCustomersTab = new Tab("Manage Customers");

        // Set the content of the tab using the method that creates the customer management UI
        manageCustomersTab.setContent(createManageCustomersUI());

        // Set up the TabPane and add the "Manage Customers" tab
        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(manageCustomersTab);

        // Set the TabPane as the content of this view
        this.getChildren().add(tabPane);
    }



    private ObservableList<Customer> customerList = FXCollections.observableArrayList();  // Example list, fetch real data as needed

    private VBox createManageCustomersUI() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // Declare and initialize the searchTextField here
        TextField searchTextField = new TextField();
        searchTextField.setPromptText("Search by Customer ID, Name, or Email");
        searchTextField.setVisible(false);  // Initially hidden
        searchTextField.setPrefWidth(300);
        searchTextField.setMaxHeight(30);

        // Fetch customers from the service
        ObservableList<Customer> customerList = FXCollections.observableArrayList(fetchAllCustomers());
        customerTable.setItems(customerList);  // Set the customer list to the table

        // Create the first inner HBox for the title
        HBox titleHBox = new HBox(10);
        Label titleLabel = new Label("Customer Management");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        titleHBox.getChildren().add(titleLabel);
        titleHBox.setAlignment(Pos.CENTER_LEFT);  // Align title to the left
        HBox.setHgrow(titleLabel, Priority.ALWAYS);  // Allow title to take up available space

        // Create the second inner HBox for the buttons (Add New Customer, Refresh, Search)
        HBox buttonsHBox = new HBox(10);
        Button addCustomerButton = new Button("Add New Customer");
        addCustomerButton.setOnAction(e -> openAddCustomerDialog());
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshCustomerTable());

        // Create the magnifying glass button for search and add to the buttonsHBox
        Button magnifyingGlassButton = new Button("🔍");
        magnifyingGlassButton.setStyle("-fx-font-size: 13px;");
        magnifyingGlassButton.setOnAction(e -> toggleSearchVisibility(searchTextField));  // Action to toggle search field visibility

        buttonsHBox.getChildren().addAll(magnifyingGlassButton, addCustomerButton, refreshButton);
        buttonsHBox.setAlignment(Pos.CENTER_RIGHT);  // Align buttons to the right

        // Create an outer HBox to hold both the title and buttons
        HBox outerHBox = new HBox(200);  // Reduced space between the elements
        outerHBox.getChildren().addAll(titleHBox, buttonsHBox);
        outerHBox.setAlignment(Pos.CENTER_LEFT);  // Align the entire HBox to the left

        // Add a listener to filter the customer list based on search input
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearchInput(newValue, customerList);
        });

        // Add listener to close the search bar if clicked outside
        searchTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                closeSearchArea(searchTextField);  // Close the search text field when focus is lost
            }
        });

        // Create an HBox for the search box (not separately needed anymore)
        HBox searchBox = new HBox(10);
        searchBox.getChildren().add(searchTextField);
        searchBox.setAlignment(Pos.CENTER);  // Align the search field to the left

        // Add the outer HBox, search box, and customer table to the layout
        layout.getChildren().addAll(outerHBox, searchBox, customerTable);

        return layout;
    }


    private void toggleSearchVisibility(TextField searchTextField) {
        searchTextField.setVisible(!searchTextField.isVisible());
        if (searchTextField.isVisible()) {
            searchTextField.requestFocus();
        }
    }

    private void closeSearchArea(TextField searchTextField) {
        searchTextField.setVisible(false);  // Hide the search text field when clicked outside
    }

    private void handleSearchInput(String query, ObservableList<Customer> customerList) {
        ObservableList<Customer> filteredCustomers = FXCollections.observableArrayList();

        for (Customer customer : customerList) {
            if (customer.getCustomerId().toLowerCase().contains(query.toLowerCase()) ||
                    customer.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                    customer.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                    customer.getEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredCustomers.add(customer);
            }
        }

        // Set the filtered customer list to the table
        customerTable.setItems(filteredCustomers);
    }










    // Fetch customers from the service
    // Fetch customers from the service
    public ObservableList<Customer> fetchAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers(); // Get customers from service
            return FXCollections.observableArrayList(customers); // Convert to ObservableList
        } catch (Exception e) { // Catch generic exceptions instead
            logger.log(Level.WARNING, "Error fetching customers: ", e);
            return FXCollections.observableArrayList(); // Return an empty list in case of error
        }
    }






    private void openAddCustomerDialog() {
        // Create and show the dialog if not already created
        if (dialogStage == null) {
            Window owner = getScene() != null ? getScene().getWindow() : null;
            createAddCustomerDialog(owner);
        }

        dialogStage.showAndWait();
    }


    private void createCustomer() {
        // Extract and validate loyalty level
        String loyaltyLevel = loyaltyLevelComboBox.getValue();
        if (loyaltyLevel == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Loyalty Level is required", "Please select a loyalty level.");
            return;
        }

        // Extract and validate other fields
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phoneNumber = phoneField.getText().trim();
        String address = addressField.getText().trim();
        LocalDate dob = dobPicker.getValue();

        // Validate mandatory fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Missing Required Fields", "Please fill out all fields.");
            return;
        }

        if (dob == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Date of Birth is required", "Please select a valid date.");
            return;
        }

        String statusString = statusComboBox.getValue();
        if (statusString == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Status is required", "Please select a status.");
            return;
        }

        String notes = notesField.getText().trim();
        int loyaltyPoints = 0; // Default loyalty points

        // Convert status string to enum
        Status status = Status.valueOf(statusString);

        // Create a new customer object
        Customer customer = new Customer(
                "", firstName, lastName, email, phoneNumber, address, dob,
                status, LocalDate.now(), loyaltyPoints, loyaltyLevel, notes
        );

        // Call the controller to create the customer
        boolean success = customerController.createCustomer(customer);

        // Show success or failure message
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer Created Successfully",
                    "Customer ID: " + customer.getCustomerId() +
                            "\nName: " + customer.getFirstName() + " " + customer.getLastName());

            clearFields(); // Reset all input fields and dropdowns
            closeAddCustomerDialog(); // Close the dialog window
        } else {
            showAlert(Alert.AlertType.ERROR, "Creation Failed", "Unable to Create Customer",
                    "An error occurred. Please try again.");
        }
    }

    // Helper method to clear all fields and reset dropdown prompts
    private void clearFields() {
        // Clear text fields
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        notesField.clear();

        // Clear date picker
        dobPicker.setValue(null);

        // Reset combo boxes
        statusComboBox.setValue(null); // This resets to show the prompt text
        loyaltyLevelComboBox.setValue(null); // Same here
    }



    private void closeAddCustomerDialog() {
        // Close the dialog when cancel or after creation
        dialogStage.close();
    }

    public void createAddCustomerDialog(Window owner) {
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

        dobPicker = new DatePicker();
        dobPicker.setPromptText("Date of Birth (YYYY-MM-DD)");

        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Active", "Inactive");
        statusComboBox.setPromptText("Select Status");

        loyaltyLevelField = new TextField();
        loyaltyLevelField.setPromptText("Loyalty Level");

        loyaltyLevelComboBox = new ComboBox<>();
        loyaltyLevelComboBox.setPromptText("Select Loyalty Level");
        loyaltyLevelComboBox.getItems().addAll("Bronze", "Silver", "Gold");

        notesField = new TextField();
        notesField.setPromptText("Enter Notes");

        createButton = new Button("Create Customer");
        createButton.setOnAction(event -> createCustomer());

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> dialogStage.close());

        dialogLayout.getChildren().addAll(
                firstNameField, lastNameField, emailField, phoneField,
                addressField, dobPicker, statusComboBox, loyaltyLevelComboBox,
                notesField, createButton, cancelButton
        );

        dialogLayout.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(dialogLayout);
        scrollPane.setFitToWidth(true);

        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        if (owner != null) {
            dialogStage.initOwner(owner);
        } else {
            System.out.println("Warning: No owner window provided. Dialog owner not set.");
        }

        dialogStage.setWidth(450);
        dialogStage.setHeight(400);
        dialogStage.setResizable(false);
        dialogStage.setTitle("Add Customer");
        dialogStage.setScene(new Scene(scrollPane));
        dialogStage.showAndWait();
    }



    public TableView<Customer> createCustomerTable() {
        // Create TableView for Customer
        TableView<Customer> customerTable = new TableView<>();
        // Define Customer ID column
        TableColumn<Customer, String> idColumn = new TableColumn<>("Customer ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        // Define Name column (First and Last Name combined)
        TableColumn<Customer, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(customer ->
                new SimpleStringProperty(customer.getValue().getFirstName() + " " + customer.getValue().getLastName()));

        // Define Email column
        TableColumn<Customer, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Define Loyalty Level column
        TableColumn<Customer, String> loyaltyLevelColumn = new TableColumn<>("Loyalty Level");
        loyaltyLevelColumn.setCellValueFactory(new PropertyValueFactory<>("loyaltyLevel"));

        // Define Edit Button column
        TableColumn<Customer, Void> editColumn = new TableColumn<>("Edit");
        editColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    // Call method to open edit dialog and pass customer details
                    openEditCustomerDialog(customer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        });

        // Define Delete Button column
        TableColumn<Customer, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    // Call method to delete customer from the database
                    deleteCustomer(customer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        // Add columns to the table
        customerTable.getColumns().addAll(idColumn, nameColumn, emailColumn, loyaltyLevelColumn, editColumn, deleteColumn);

        // Set data to the table
        customerTable.setItems(FXCollections.observableArrayList(customerController.getAllCustomers()));

        return customerTable;
    }

    private void openEditCustomerDialog(Customer customer) {
        // Create a dialog for editing customer details
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Edit Customer");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Create the form fields (TextFields, DatePicker, etc.)
        TextField firstNameField = new TextField(customer.getFirstName());
        TextField lastNameField = new TextField(customer.getLastName());
        TextField emailField = new TextField(customer.getEmail());
        TextField phoneField = new TextField(customer.getPhoneNumber());
        TextField addressField = new TextField(customer.getAddress());
        DatePicker dobPicker = new DatePicker(customer.getDateOfBirth());
        ComboBox<Status> statusComboBox = new ComboBox<>(FXCollections.observableArrayList(Status.values()));
        statusComboBox.setValue(customer.getStatus());

        // Set up the layout for the dialog
        VBox dialogContent = new VBox(10,
                new Label("First Name"), firstNameField,
                new Label("Last Name"), lastNameField,
                new Label("Email"), emailField,
                new Label("Phone"), phoneField,
                new Label("Address"), addressField,
                new Label("Date of Birth"), dobPicker,
                new Label("Status"), statusComboBox
        );
        dialogContent.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(dialogContent);

        // Convert the result into a Customer object after clicking "Save"
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                // Validate inputs
                if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
                    showError("First Name and Last Name cannot be empty.");
                    return null;
                }
                if (!isValidEmail(emailField.getText())) {
                    showError("Invalid email address.");
                    return null;
                }

                // Retrieve the updated customer details from the fields
                customer.setFirstName(firstNameField.getText());
                customer.setLastName(lastNameField.getText());
                customer.setEmail(emailField.getText());
                customer.setPhoneNumber(phoneField.getText());
                customer.setAddress(addressField.getText());
                customer.setDateOfBirth(dobPicker.getValue());
                customer.setStatus(statusComboBox.getValue());

                // Return the updated customer
                return customer;
            }
            return null; // Return null if "Cancel" is clicked
        });

        // Show the dialog and wait for the result
        Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(updatedCustomer -> {
            // Handle the updated customer (e.g., save changes)
            boolean success = customerController.updateCustomer(updatedCustomer);
            if (success) {
                refreshCustomerTable(); // Call the refresh method
            } else {
                showError("Error updating customer.");
            }
        });
    }


    // Helper method for email validation
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    }

    // Method to refresh the customer table
    private void refreshCustomerTable() {
        customerTable.setItems(FXCollections.observableArrayList(customerController.getAllCustomers()));
    }




    private void deleteCustomer(Customer customer) {
        if (customerTable != null) {  // Check if customerTable is initialized
            // Create a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Customer");
            alert.setHeaderText("Are you sure you want to delete this customer?");
            alert.setContentText("This action cannot be undone.");

            // Show the dialog and wait for a response
            Optional<ButtonType> result = alert.showAndWait();

            // If the user confirms, proceed with deletion
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Call the deleteCustomer method in CustomerController, passing the customer ID as a String
                boolean success = customerController.deleteCustomer(String.valueOf(customer.getCustomerId()));

                // Provide feedback based on success or failure
                if (success) {
                    // Update table data after deletion
                    refreshCustomerTable();
                } else {
                    // Display error message if deletion fails
                    showError("Error: Could not delete customer.");
                }
            }
        } else {
            // Display error if customerTable is not initialized
            showError("Error: customerTable is not initialized.");
        }
    }




    private void clearSearchInput(TextArea searchTextArea, ObservableList<Customer> customerList) {
        searchTextArea.clear();
        customerTable.setItems(customerList);  // Reset to full list
    }



    // Helper method to show error messages
    private void showError(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }




    private void showAlert(AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}

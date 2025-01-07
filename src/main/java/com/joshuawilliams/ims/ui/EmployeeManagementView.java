package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.model.Employee;
import com.joshuawilliams.ims.service.EmployeeService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.time.ZoneId;






public class EmployeeManagementView {
    private EmployeeService employeeService;
    private TableView<Employee> employeeTable;


    // Constructor
    public EmployeeManagementView(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public BorderPane createEmployeeManagementLayout(Stage ownerStage) {
        BorderPane borderPane = new BorderPane();

        TabPane tabPane = new TabPane();

        // Employee Management Tab
        Tab employeeTab = new Tab("Employee Management");
        employeeTab.setContent(createEmployeeManagementView(ownerStage)); // Pass ownerStage here
        tabPane.getTabs().add(employeeTab);
        tabPane.getSelectionModel().select(employeeTab); // Set as default selected tab

        // Department Management Tab
        Tab departmentTab = new Tab("Department Management");
        departmentTab.setContent(createDepartmentManagementView()); // Ensure single method definition
        tabPane.getTabs().add(departmentTab);

        borderPane.setCenter(tabPane);
        return borderPane;
    }




    public VBox createEmployeeManagementView(Stage ownerStage) {
        employeeTable = new TableView<>();
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Add Employee Button
        Button addButton = new Button("Add Employee");
        addButton.setOnAction(e -> showAddEmployeeDialog(ownerStage)); // Pass ownerStage for modal behavior

        // Employee Table View
        TableView<Employee> employeeTable = new TableView<>();
        employeeTable.setItems(FXCollections.observableArrayList(employeeService.getAllEmployees()));

        // Table Columns
        TableColumn<Employee, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Employee, String> departmentColumn = new TableColumn<>("Department");
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<Employee, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<Employee, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        // Actions Column for Edit and Delete
        TableColumn<Employee, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                // Edit Button Action
                editButton.setOnAction(e -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    showEditEmployeeDialog(ownerStage, employee);
                });

                // Delete Button Action
                deleteButton.setOnAction(e -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    showDeleteConfirmationDialog(employee); // Use confirmation dialog for deletion
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton); // Horizontal layout for buttons
                    setGraphic(buttons);
                }
            }
        });

        // Add Columns to Table
        employeeTable.getColumns().addAll(
                idColumn, nameColumn, departmentColumn, roleColumn, emailColumn, salaryColumn, actionsColumn
        );

        // Add Table and Button to VBox
        vbox.getChildren().addAll(addButton, employeeTable);

        return vbox;
    }


    private void refreshTable() {
        List<Employee> employees = employeeService.getAllEmployees();
        employeeTable.setItems(FXCollections.observableArrayList(employees));
    }



    private void showEditEmployeeDialog(Stage ownerStage, Employee selectedEmployee) {
        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.ERROR, "No Employee Selected", "Please select an employee to edit.");
            return;
        }

        // Load the edit dialog
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Edit Employee");
        dialog.setHeaderText("Edit Employee Details");

        // Create a custom form using GridPane
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        TextField nameField = new TextField(selectedEmployee.getName());
        TextField emailField = new TextField(selectedEmployee.getEmail());

        // Convert java.sql.Date to LocalDate
        java.sql.Date sqlDate = selectedEmployee.getDateOfBirth();
        LocalDate localDate = sqlDate != null ? sqlDate.toLocalDate() : null;  // Convert to LocalDate
        DatePicker dobPicker = new DatePicker(localDate); // DatePicker expects LocalDate

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Date of Birth:"), 0, 2);
        grid.add(dobPicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                selectedEmployee.setName(nameField.getText());
                selectedEmployee.setEmail(emailField.getText());

                // Convert LocalDate (from DatePicker) to java.sql.Date before saving
                if (dobPicker.getValue() != null) {
                    selectedEmployee.setDateOfBirth(java.sql.Date.valueOf(dobPicker.getValue())); // LocalDate -> java.sql.Date
                } else {
                    selectedEmployee.setDateOfBirth(null);
                }
                return selectedEmployee;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedEmployee -> {
            try {
                employeeService.updateEmployee(updatedEmployee);
                refreshTable(); // Reload the table data
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee details updated successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update employee: " + e.getMessage());
            }
        });
    }







    private void showDeleteConfirmationDialog(Employee selectedEmployee) {
        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.ERROR, "No Employee Selected", "Please select an employee to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Employee");
        alert.setHeaderText("Are you sure you want to delete this employee?");
        alert.setContentText("Employee: " + selectedEmployee.getName() + "\nID: " + selectedEmployee.getId());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                employeeService.deleteEmployee(selectedEmployee.getId());
                refreshTable(); // Reload the table data
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee deleted successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete employee: " + e.getMessage());
            }
        }
    }






    private VBox createDepartmentManagementView() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Input for departments
        TextField departmentNameField = new TextField();
        departmentNameField.setPromptText("New Department Name");

        Button addDepartmentButton = new Button("Add Department");
        addDepartmentButton.setOnAction(e -> {
            String departmentName = departmentNameField.getText().trim();
            if (departmentName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Department name cannot be empty.");
                return;
            }
            addDepartment(departmentName);
            departmentNameField.clear();
        });

        // Input for roles
        TextField roleNameField = new TextField();
        roleNameField.setPromptText("New Role Name");

        Button addRoleButton = new Button("Add Role");
        addRoleButton.setOnAction(e -> {
            String roleName = roleNameField.getText().trim();
            if (roleName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Role name cannot be empty.");
                return;
            }
            addRole(roleName);
            roleNameField.clear();
        });

        vbox.getChildren().addAll(new Label("Department Management"), departmentNameField, addDepartmentButton,
                new Label("Role Management"), roleNameField, addRoleButton);

        return vbox;
    }


    private void showAddEmployeeDialog(Stage ownerStage) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add Employee");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ownerStage);
        dialogStage.setResizable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField idField = new TextField();
        idField.setPromptText("ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        ComboBox<String> departmentDropdown = getDepartmentDropdown();
        departmentDropdown.setPromptText("Select Department");

        ComboBox<String> roleDropdown = getRoleDropdown();
        roleDropdown.setPromptText("Select Role");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Salary");

        DatePicker dateOfBirthPicker = new DatePicker();
        dateOfBirthPicker.setPromptText("Date of Birth");

        DatePicker hireDatePicker = new DatePicker();
        hireDatePicker.setPromptText("Hire Date");

        TextField addressField = new TextField();
        addressField.setPromptText("Address");

        TextField managerIdField = new TextField();
        managerIdField.setPromptText("Manager ID");

        TextField phoneNumberField = new TextField();
        phoneNumberField.setPromptText("Phone Number");

        ComboBox<String> statusDropdown = getStatusDropdown();
        statusDropdown.setPromptText("Select Status");

        ComboBox<String> employmentTypeDropdown = getEmploymentTypeDropdown();
        employmentTypeDropdown.setPromptText("Select Employment Type");

        TextField performanceReviewField = new TextField();
        performanceReviewField.setPromptText("Performance Review");

        TextField emergencyContactField = new TextField();
        emergencyContactField.setPromptText("Emergency Contact");

        TextField nationalIdField = new TextField();
        nationalIdField.setPromptText("National ID");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            if (nameField.getText().isEmpty() || departmentDropdown.getValue() == null ||
                    roleDropdown.getValue() == null || emailField.getText().isEmpty() ||
                    statusDropdown.getValue() == null || hireDatePicker.getValue() == null ||
                    employmentTypeDropdown.getValue() == null || salaryField.getText().isEmpty()) {
                showAlert(AlertType.ERROR, "Invalid Input", "Please fill in all required fields.");
                return;
            }

            try {
                Employee employee = new Employee(
                        idField.getText(), nameField.getText(), roleDropdown.getValue(),
                        departmentDropdown.getValue(), phoneNumberField.getText(), emailField.getText(),
                        statusDropdown.getValue(),
                        dateOfBirthPicker.getValue() != null ? Date.valueOf(dateOfBirthPicker.getValue()) : null,
                        Date.valueOf(hireDatePicker.getValue()), addressField.getText(),
                        managerIdField.getText(), Double.parseDouble(salaryField.getText()),
                        performanceReviewField.getText(), employmentTypeDropdown.getValue(),
                        emergencyContactField.getText(), nationalIdField.getText()
                );
                employeeService.addEmployee(employee);
                showAlert(AlertType.INFORMATION, "Employee Added", "Employee has been added successfully.");
                dialogStage.close();
            } catch (Exception ex) {
                showAlert(AlertType.ERROR, "Error Adding Employee", "An error occurred while adding the employee: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(
                idField, nameField, departmentDropdown, roleDropdown, emailField, salaryField,
                dateOfBirthPicker, hireDatePicker, addressField, managerIdField, phoneNumberField,
                statusDropdown, employmentTypeDropdown, performanceReviewField, emergencyContactField, nationalIdField, saveButton
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 430, 460);
        dialogStage.setScene(scene);
        dialogStage.show();
    }




    private void addEmployee(TextField idField, TextField nameField, ComboBox<String> departmentDropdown, ComboBox<String> roleDropdown,
                             ComboBox<String> statusDropdown, ComboBox<String> employmentTypeDropdown, TextField emailField,
                             TextField salaryField, DatePicker dateOfBirthPicker, DatePicker hireDatePicker, TextField addressField,
                             TextField managerIdField, TextField phoneNumberField, ComboBox<String> performanceReviewDropdown,
                             TextField emergencyContactField, TextField nationalIdField) {
        try {
            String id = idField.getText();
            String name = nameField.getText();
            String role = roleDropdown.getValue();
            String department = departmentDropdown.getValue();
            String phoneNumber = phoneNumberField.getText();
            String email = emailField.getText();
            String status = statusDropdown.getValue();
            String dateOfBirth = dateOfBirthPicker.getValue().toString();
            String hireDate = hireDatePicker.getValue().toString();
            String address = addressField.getText();
            String managerId = managerIdField.getText();
            double salary = Double.parseDouble(salaryField.getText());
            String performanceReview = performanceReviewDropdown.getValue();
            String employmentType = employmentTypeDropdown.getValue();
            String emergencyContact = emergencyContactField.getText();
            String nationalId = nationalIdField.getText();

            Employee employee = new Employee(id, name, role, department, phoneNumber, email, status,
                    Date.valueOf(dateOfBirth), Date.valueOf(hireDate), address,
                    managerId, salary, performanceReview, employmentType,
                    emergencyContact, nationalId);

            employeeService.addEmployee(employee);
            showAlert(AlertType.INFORMATION, "Employee Added", "Employee has been added successfully.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error Adding Employee", "An error occurred while adding the employee: " + e.getMessage());
        }
    }



    public void addDepartment(String departmentName) {
        // Logic to add the department (e.g., update the database or local list)
        employeeService.addDepartment(departmentName);
    }

    public void addRole(String roleName) {
        // Logic to add the role (e.g., update the database or local list)
        employeeService.addRole(roleName);
    }


    private ComboBox<String> getDepartmentDropdown() {
        ObservableList<String> departments = FXCollections.observableArrayList(
                "Sales", "IT", "HR", "Marketing", "Finance"
        );
        return new ComboBox<>(departments);
    }

    private ComboBox<String> getRoleDropdown() {
        ObservableList<String> roles = FXCollections.observableArrayList(
                "Manager", "Developer", "Analyst", "Clerk", "Salesperson"
        );
        return new ComboBox<>(roles);
    }

    private ComboBox<String> getStatusDropdown() {
        ObservableList<String> status = FXCollections.observableArrayList(
                "Active", "Inactive"
        );
        return new ComboBox<>(status);
    }

    private ComboBox<String> getEmploymentTypeDropdown() {
        ObservableList<String> employmentTypes = FXCollections.observableArrayList(
                "Full-time", "Part-time", "Contract", "Internship"
        );
        return new ComboBox<>(employmentTypes);
    }

    private ComboBox<String> getPerformanceReviewDropdown() {
        ObservableList<String> performanceReviews = FXCollections.observableArrayList(
                "Excellent", "Good", "Satisfactory", "Needs Improvement"
        );
        return new ComboBox<>(performanceReviews);
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}

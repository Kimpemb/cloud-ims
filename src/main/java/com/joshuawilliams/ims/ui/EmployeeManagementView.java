package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.dao.DepartmentDao;
import com.joshuawilliams.ims.dao.EmployeeDao;
import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.model.Department;
import com.joshuawilliams.ims.model.Employee;
import com.joshuawilliams.ims.service.EmployeeService;

import com.joshuawilliams.ims.utils.EmailValidator;
import com.joshuawilliams.ims.utils.PasswordUtils;
import com.joshuawilliams.ims.utils.UIHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

import java.sql.*;
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
    private Connection connection;

    // Constructor
    public EmployeeManagementView(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public BorderPane createEmployeeManagementLayout(Stage ownerStage) {
        BorderPane borderPane = new BorderPane();

        TabPane tabPane = new TabPane();

        // Employee Management Tab
        Tab employeeTab = new Tab("Employee Management");
        employeeTab.setClosable(false); // Prevent accidental tab closure
        employeeTab.setContent(createEmployeeManagementView(ownerStage)); // Pass ownerStage here
        tabPane.getTabs().add(employeeTab);

        // Department Management Tab
        Tab departmentTab = new Tab("Department Management");
        departmentTab.setClosable(false); // Prevent accidental tab closure
        departmentTab.setContent(createDepartmentManagementView(ownerStage)); // Pass ownerStage
        tabPane.getTabs().add(departmentTab);

        // Set the default selected tab
        tabPane.getSelectionModel().select(employeeTab);

        // Add TabPane to the center of BorderPane
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

        TableColumn<Employee, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory((new PropertyValueFactory<>("status")));

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
                idColumn, nameColumn, departmentColumn, roleColumn, emailColumn, salaryColumn, actionsColumn, statusColumn
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

        // Text fields for employee details
        TextField nameField = new TextField(selectedEmployee.getName());
        TextField emailField = new TextField(selectedEmployee.getEmail());

        ComboBox<String> departmentDropdown = getDepartmentDropdown();
        departmentDropdown.setValue(selectedEmployee.getDepartment());

        ComboBox<String> roleDropdown = getRoleDropdown();
        roleDropdown.setValue(selectedEmployee.getRole());

        TextField salaryField = new TextField(String.valueOf(selectedEmployee.getSalary()));

        DatePicker dobPicker = new DatePicker(selectedEmployee.getDateOfBirth().toLocalDate());
        DatePicker hireDatePicker = new DatePicker(selectedEmployee.getHireDate().toLocalDate());

        TextField addressField = new TextField(selectedEmployee.getAddress());
        TextField managerIdField = new TextField(selectedEmployee.getManagerId());
        TextField phoneNumberField = new TextField(selectedEmployee.getPhoneNumber());

        ComboBox<String> statusDropdown = getStatusDropdown();
        statusDropdown.setValue(selectedEmployee.getStatus());

        ComboBox<String> employmentTypeDropdown = getEmploymentTypeDropdown();
        employmentTypeDropdown.setValue(selectedEmployee.getEmploymentType());

        TextField performanceReviewField = new TextField(selectedEmployee.getPerformanceReview());
        TextField emergencyContactField = new TextField(selectedEmployee.getEmergencyContact());
        TextField nationalIdField = new TextField(selectedEmployee.getNationalId());

        // Password field (optional)
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Leave blank to keep current password");

        // Add labels and fields to the grid
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Department:"), 0, 2);
        grid.add(departmentDropdown, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleDropdown, 1, 3);
        grid.add(new Label("Salary:"), 0, 4);
        grid.add(salaryField, 1, 4);
        grid.add(new Label("Date of Birth:"), 0, 5);
        grid.add(dobPicker, 1, 5);
        grid.add(new Label("Hire Date:"), 0, 6);
        grid.add(hireDatePicker, 1, 6);
        grid.add(new Label("Address:"), 0, 7);
        grid.add(addressField, 1, 7);
        grid.add(new Label("Manager ID:"), 0, 8);
        grid.add(managerIdField, 1, 8);
        grid.add(new Label("Phone Number:"), 0, 9);
        grid.add(phoneNumberField, 1, 9);
        grid.add(new Label("Status:"), 0, 10);
        grid.add(statusDropdown, 1, 10);
        grid.add(new Label("Employment Type:"), 0, 11);
        grid.add(employmentTypeDropdown, 1, 11);
        grid.add(new Label("Performance Review:"), 0, 12);
        grid.add(performanceReviewField, 1, 12);
        grid.add(new Label("Emergency Contact:"), 0, 13);
        grid.add(emergencyContactField, 1, 13);
        grid.add(new Label("National ID:"), 0, 14);
        grid.add(nationalIdField, 1, 14);
        grid.add(new Label("Password:"), 0, 15);
        grid.add(passwordField, 1, 15);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                try {
                    // Field Validation
                    if (!EmailValidator.isValidEmail(emailField.getText())) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address.");
                        return null;
                    }

                    if (!employeeService.isValidDateOfBirth(java.sql.Date.valueOf(dobPicker.getValue()))) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Date of Birth", "Employee must be at least 18 years old.");
                        return null;
                    }

                    if (!employeeService.isValidHireDate(java.sql.Date.valueOf(hireDatePicker.getValue()))) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Hire Date", "Hire date cannot be in the future.");
                        return null;
                    }

                    selectedEmployee.setName(nameField.getText());
                    selectedEmployee.setEmail(emailField.getText());
                    selectedEmployee.setDepartment(departmentDropdown.getValue());
                    selectedEmployee.setRole(roleDropdown.getValue());
                    selectedEmployee.setSalary(Double.parseDouble(salaryField.getText()));
                    selectedEmployee.setDateOfBirth(java.sql.Date.valueOf(dobPicker.getValue()));
                    selectedEmployee.setHireDate(java.sql.Date.valueOf(hireDatePicker.getValue()));
                    selectedEmployee.setAddress(addressField.getText());
                    selectedEmployee.setManagerId(managerIdField.getText());
                    selectedEmployee.setPhoneNumber(phoneNumberField.getText());
                    selectedEmployee.setStatus(statusDropdown.getValue());
                    selectedEmployee.setEmploymentType(employmentTypeDropdown.getValue());
                    selectedEmployee.setPerformanceReview(performanceReviewField.getText());
                    selectedEmployee.setEmergencyContact(emergencyContactField.getText());
                    selectedEmployee.setNationalId(nationalIdField.getText());

                    // Password validation and hashing
                    String newPassword = passwordField.getText();
                    if (newPassword != null && !newPassword.isEmpty()) {
                        if (!employeeService.isValidPassword(newPassword)) {
                            showAlert(Alert.AlertType.ERROR, "Invalid Password",
                                    "Password must be at least 8 characters, including a number and a special character.");
                            return null;
                        }
                        selectedEmployee.setPassword(PasswordUtils.hashPassword(newPassword));
                    }

                    return selectedEmployee;

                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid salary.");
                }
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






    private VBox createDepartmentManagementView(Stage ownerStage) {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(10));

        // Section: Department Management
        Label departmentManagementLabel = new Label("Department Management");
        departmentManagementLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox departmentControls = new HBox(10);
        departmentControls.setAlignment(Pos.CENTER_LEFT);


        Button addDepartmentButton = new Button("Add Department");
        addDepartmentButton.setOnAction(e -> showAddDepartmentDialog(ownerStage));

        departmentControls.getChildren().addAll(addDepartmentButton);

        // Section: Role Management
        Label roleManagementLabel = new Label("Role Management");
        roleManagementLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox roleControls = new HBox(10);
        roleControls.setAlignment(Pos.CENTER_LEFT);

        TextField roleNameField = new TextField();
        roleNameField.setPromptText("Role Name");

        Button addRoleButton = new Button("Add Role");
        addRoleButton.setOnAction(e -> {
            String roleName = roleNameField.getText().trim();
            if (roleName.isEmpty()) {
                UIHelper.showAlert(Alert.AlertType.ERROR, "Validation Error", "Role name cannot be empty.");
                return;
            }

            Connection connection = DatabaseConnection.getConnection();  // Get the connection
            EmployeeDao employeeDao = new EmployeeDao(connection); // Pass the connection
            EmployeeService employeeService = new EmployeeService(employeeDao);

            employeeService.addRole(roleName);

            UIHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Role added successfully!");
            roleNameField.clear();
        });

        roleControls.getChildren().addAll(new Label("Role Name:"), roleNameField, addRoleButton);

        // Section: Departments Table
        Label departmentsTableLabel = new Label("Departments Table");
        departmentsTableLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<Department> departmentTable = createDepartmentTableView();

        // Add all sections to the main VBox
        vbox.getChildren().addAll(
                departmentManagementLabel,
                departmentControls,
                roleManagementLabel,
                roleControls,
                new Separator(),
                departmentsTableLabel,
                departmentTable
        );

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

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            if (nameField.getText().isEmpty() || departmentDropdown.getValue() == null ||
                    roleDropdown.getValue() == null || emailField.getText().isEmpty() ||
                    statusDropdown.getValue() == null || hireDatePicker.getValue() == null ||
                    employmentTypeDropdown.getValue() == null || salaryField.getText().isEmpty() ||
                    passwordField.getText().isEmpty()) {
                showAlert(AlertType.ERROR, "Invalid Input", "Please fill in all required fields.");
                return;
            }

            try {
                Employee employee = new Employee(
                        null,
                        nameField.getText(),
                        roleDropdown.getValue(),
                        departmentDropdown.getValue(),
                        phoneNumberField.getText(),
                        emailField.getText(),
                        statusDropdown.getValue(),
                        dateOfBirthPicker.getValue() != null ? Date.valueOf(dateOfBirthPicker.getValue()) : null,
                        Date.valueOf(hireDatePicker.getValue()),
                        addressField.getText(),
                        managerIdField.getText(),
                        Double.parseDouble(salaryField.getText()),
                        performanceReviewField.getText(),
                        employmentTypeDropdown.getValue(),
                        emergencyContactField.getText(),
                        nationalIdField.getText(),
                        passwordField.getText() // Added password field
                );
                employeeService.addEmployee(employee);
                showAlert(AlertType.INFORMATION, "Employee Added", "Employee has been added successfully.");
                dialogStage.close();
            } catch (Exception ex) {
                showAlert(AlertType.ERROR, "Error Adding Employee", "An error occurred while adding the employee: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(
                nameField, departmentDropdown, roleDropdown, emailField, salaryField,
                dateOfBirthPicker, hireDatePicker, addressField, managerIdField, phoneNumberField,
                statusDropdown, employmentTypeDropdown, performanceReviewField, emergencyContactField,
                nationalIdField, passwordField, saveButton
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 430, 500); // Increased height to accommodate the password field
        dialogStage.setScene(scene);
        dialogStage.show();
    }


    private TableView<Department> createDepartmentTableView() {
        TableView<Department> tableView = new TableView<>();

        // Create columns for the TableView
        TableColumn<Department, String> idColumn = createColumn("Department ID", "id");
        TableColumn<Department, String> nameColumn = createColumn("Department Name", "name");
        TableColumn<Department, String> codeColumn = createColumn("Department Code", "code");
        TableColumn<Department, String> managerColumn = createColumn("Manager Name", "managerName");
        TableColumn<Department, String> emailColumn = createColumn("Email", "email");
        TableColumn<Department, String> locationColumn = createColumn("Location", "location");
        TableColumn<Department, String> statusColumn = createColumn("Status", "status");

        // Add action buttons column
        TableColumn<Department, Void> actionColumn = createActionColumn();

        // Add all columns to the TableView
        tableView.getColumns().addAll(
                idColumn, nameColumn, codeColumn, managerColumn, emailColumn, locationColumn, statusColumn, actionColumn
        );

        // Load department data into the TableView
        DepartmentDao departmentDao = new DepartmentDao();
        List<Department> departmentData = departmentDao.getAllDepartments(); // Fetch all departments from database
        tableView.getItems().addAll(departmentData);

        return tableView;
    }

    private TableColumn<Department, String> createColumn(String title, String property) {
        TableColumn<Department, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    private TableColumn<Department, Void> createActionColumn() {
        TableColumn<Department, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox actionBox = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(e -> {
                    Department department = getTableView().getItems().get(getIndex());
                    showEditDepartmentDialog(department);
                });

                deleteButton.setOnAction(e -> {
                    Department department = getTableView().getItems().get(getIndex());
                    deleteDepartment(department);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBox);
            }
        });
        return actionColumn;
    }



    // Show the Add Department Dialog
    private void showAddDepartmentDialog(Stage ownerStage) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add Department");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ownerStage);
        dialogStage.setResizable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Input fields for adding a department
        TextField nameField = new TextField();
        nameField.setPromptText("Department Name");

        TextField codeField = new TextField();
        codeField.setPromptText("Department Code");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField managerField = new TextField();
        managerField.setPromptText("Manager Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField locationField = new TextField();
        locationField.setPromptText("Location");

        ComboBox<String> statusDropdown = new ComboBox<>();
        statusDropdown.getItems().addAll("Active", "Inactive");
        statusDropdown.setPromptText("Select Status");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            if (nameField.getText().isEmpty() || codeField.getText().isEmpty() || statusDropdown.getValue() == null) {
                UIHelper.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please fill in all required fields.");
                return;
            }

            DepartmentDao departmentDao = new DepartmentDao();
            departmentDao.addDepartment(
                    nameField.getText(),
                    codeField.getText(),
                    descriptionField.getText(),
                    managerField.getText(),
                    emailField.getText(),
                    locationField.getText(),
                    statusDropdown.getValue()
            );

            // Display confirmation message
            UIHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Department added successfully!");

            dialogStage.close();
        });


        vbox.getChildren().addAll(
                nameField, codeField, descriptionField, managerField, emailField, locationField,
                statusDropdown, saveButton
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 400, 400);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    private void showEditDepartmentDialog(Department department) {
        Dialog<Department> dialog = new Dialog<>();
        dialog.setTitle("Edit Department");
        dialog.setHeaderText("Edit details for: " + department.getName());

        // Create form fields
        TextField nameField = new TextField(department.getName());
        TextField codeField = new TextField(department.getCode());
        TextField managerField = new TextField(department.getManagerName());
        TextField emailField = new TextField(department.getEmail());
        TextField locationField = new TextField(department.getLocation());
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Active", "Inactive");
        statusComboBox.setValue(department.getStatus());

        // Form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Department Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Department Code:"), 0, 1);
        grid.add(codeField, 1, 1);
        grid.add(new Label("Manager Name:"), 0, 2);
        grid.add(managerField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Location:"), 0, 4);
        grid.add(locationField, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusComboBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Handle Save action
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                department.setName(nameField.getText().trim());
                department.setCode(codeField.getText().trim());
                department.setManagerName(managerField.getText().trim());
                department.setEmail(emailField.getText().trim());
                department.setLocation(locationField.getText().trim());
                department.setStatus(statusComboBox.getValue());
                return department;
            }
            return null;
        });

        Optional<Department> result = dialog.showAndWait();
        result.ifPresent(updatedDepartment -> {
            DepartmentDao departmentDao = new DepartmentDao();
            departmentDao.updateDepartment(updatedDepartment); // Update department in the database
            UIHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Department updated successfully!");
        });
    }

    private void deleteDepartment(Department department) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Department");
        confirmationAlert.setHeaderText("Are you sure you want to delete the department?");
        confirmationAlert.setContentText("Department: " + department.getName());

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DepartmentDao departmentDao = new DepartmentDao();
            boolean success = departmentDao.deleteDepartment(department.getId()); // Delete department from the database
            if (success) {
                UIHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Department deleted successfully!");
            } else {
                UIHelper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete department.");
            }
        }
    }






    private ComboBox<String> getDepartmentDropdown() {
        ObservableList<String> departments = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM departments");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                departments.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            UIHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load departments: " + e.getMessage());
        }
        return new ComboBox<>(departments);
    }

    private ComboBox<String> getRoleDropdown() {
        ObservableList<String> roles = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT role_name FROM roles");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                roles.add(rs.getString("role_name"));
            }
        } catch (SQLException e) {
            UIHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load roles: " + e.getMessage());
        }
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

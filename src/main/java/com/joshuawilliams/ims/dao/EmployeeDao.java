package com.joshuawilliams.ims.dao;

import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.model.Employee;
import com.joshuawilliams.ims.utils.UIHelper;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDao {
    // Use SLF4J LoggerFactory for logging
    public static final Logger logger = LoggerFactory.getLogger(EmployeeDao.class);
    private final Connection connection;

    public EmployeeDao(Connection connection) {
        this.connection = connection;
    }

    // Retrieve all employees
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employees";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }

        } catch (SQLException e) {
            logger.error("Error fetching employees from database: ", e); // Log the exception with error level
        }

        return employees;
    }

    public Optional<Employee> getEmployeeById(String employeeId) {
        String query = "SELECT * FROM employees WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching employee by ID: {}", employeeId, e);
        }
        return Optional.empty(); // Return empty if no employee found
    }

    // Helper method to map ResultSet to Employee object
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("role_id"),
                rs.getString("department_id"),
                rs.getString("phone_number"),
                rs.getString("email"),
                rs.getString("status"),
                rs.getDate("date_of_birth"),
                rs.getDate("hire_date"),
                rs.getString("address"),
                rs.getString("manager_id"),
                rs.getDouble("salary"),
                rs.getString("performance_review"),
                rs.getString("employment_type"),
                rs.getString("emergency_contact"),
                rs.getString("national_id"),
                rs.getString("password") // Added password field
        );
    }






    // Add a new employee
    // Add a new employee
    public void addEmployee(Employee employee) {
        String query = "INSERT INTO employees (name, department_id, role_id, email, salary, date_of_birth, hire_date, " +
                "address, manager_id, phone_number, performance_review, emergency_contact, national_id, status, " +
                "employment_type, password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        logger.info("Preparing to add a new employee with the following details: "
                + "Name: " + employee.getName() + ", "
                + "Department ID: " + employee.getDepartment() + ", "
                + "Role ID: " + employee.getRole() + ", "
                + "Email: " + employee.getEmail() + ", "
                + "Salary: " + employee.getSalary() + ", "
                + "Date of Birth: " + employee.getDateOfBirth() + ", "
                + "Hire Date: " + employee.getHireDate() + ", "
                + "Address: " + employee.getAddress() + ", "
                + "Manager ID: " + employee.getManagerId() + ", "
                + "Phone Number: " + employee.getPhoneNumber() + ", "
                + "Performance Review: " + employee.getPerformanceReview() + ", "
                + "Emergency Contact: " + employee.getEmergencyContact() + ", "
                + "National ID: " + employee.getNationalId() + ", "
                + "Status: " + employee.getStatus() + ", "
                + "Employment Type: " + employee.getEmploymentType());

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            logger.info("Preparing statement with query: " + query);

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getDepartment());
            stmt.setString(3, employee.getRole());
            stmt.setString(4, employee.getEmail());
            stmt.setDouble(5, employee.getSalary());
            stmt.setDate(6, employee.getDateOfBirth() != null ? new Date(employee.getDateOfBirth().getTime()) : null);
            stmt.setDate(7, employee.getHireDate() != null ? new Date(employee.getHireDate().getTime()) : null);
            stmt.setString(8, employee.getAddress());
            stmt.setString(9, employee.getManagerId());
            stmt.setString(10, employee.getPhoneNumber());
            stmt.setString(11, employee.getPerformanceReview());
            stmt.setString(12, employee.getEmergencyContact());
            stmt.setString(13, employee.getNationalId());
            stmt.setString(14, employee.getStatus());
            stmt.setString(15, employee.getEmploymentType());
            stmt.setString(16, employee.getPassword()); // Added password field

            logger.info("Executing query...");
            stmt.executeUpdate();
            logger.info("Query executed successfully. Employee added.");
        } catch (SQLException e) {
            logger.error("Error occurred while adding a new employee: {}", e.getMessage(), e);
            throw new RuntimeException("Error adding employee: " + e.getMessage(), e);
        }
    }


    public void updateEmployee(Employee employee) {
        String query = "UPDATE employees SET name = ?, department_id = ?, role_id = ?, email = ?, salary = ?, date_of_birth = ?, " +
                "hire_date = ?, address = ?, manager_id = ?, phone_number = ?, performance_review = ?, emergency_contact = ?, " +
                "national_id = ?, status = ?, employment_type = ?";

        // Include password update if provided
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            query += ", password = ?";
        }

        query += " WHERE id = ?";

        logger.info("Initiating the process to update employee with ID: {}", employee.getId());

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Set standard parameters
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getDepartment());
            stmt.setString(3, employee.getRole());
            stmt.setString(4, employee.getEmail());
            stmt.setDouble(5, employee.getSalary());
            stmt.setDate(6, employee.getDateOfBirth() != null ? new java.sql.Date(employee.getDateOfBirth().getTime()) : null);
            stmt.setDate(7, employee.getHireDate() != null ? new java.sql.Date(employee.getHireDate().getTime()) : null);
            stmt.setString(8, employee.getAddress());
            stmt.setString(9, employee.getManagerId());
            stmt.setString(10, employee.getPhoneNumber());
            stmt.setString(11, employee.getPerformanceReview());
            stmt.setString(12, employee.getEmergencyContact());
            stmt.setString(13, employee.getNationalId());
            stmt.setString(14, employee.getStatus());
            stmt.setString(15, employee.getEmploymentType());

            int parameterIndex = 16;

            // Set password if provided
            if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
                stmt.setString(parameterIndex++, employee.getPassword());
                logger.debug("Set Password Parameter");
            }

            stmt.setString(parameterIndex, employee.getId());
            logger.debug("Set Employee ID Parameter: {}", employee.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("Employee with ID: {} updated successfully.", employee.getId());
            } else {
                logger.warn("No employee found with ID: {}", employee.getId());
            }
        } catch (SQLException e) {
            logger.error("Error occurred while updating employee with ID: {}", employee.getId(), e);
            throw new RuntimeException("Error updating employee: " + e.getMessage(), e);
        }
    }



    // Delete an employee by ID
    public void deleteEmployee(String employeeId) {
        String query = "DELETE FROM employees WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, employeeId);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("Employee with ID: {} deleted successfully.", employeeId);
            } else {
                logger.warn("No employee found with ID: {}", employeeId);
            }
        } catch (SQLException e) {
            logger.error("Error occurred while deleting employee with ID: {}", employeeId, e);
        }
    }



    // Add a new department
    public void addDepartment(String name, String code, String description, String managerName, String email, String location, String status) {
        String sql = "INSERT INTO departments (name, code, description, manager_name, email, location, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();  // Use DatabaseConnection here
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, code);
            stmt.setString(3, description);
            stmt.setString(4, managerName);
            stmt.setString(5, email);
            stmt.setString(6, location);
            stmt.setString(7, status);
            stmt.executeUpdate();
        } catch (SQLException e) {
            UIHelper.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the department: " + e.getMessage());
        }
    }



    // Add a new role
    public void addRole(String roleName) {
        String sql = "INSERT INTO roles (role_name) VALUES (?)"; // Make sure 'role_name' is used here
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roleName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            UIHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add role: " + e.getMessage());
        }
    }

}

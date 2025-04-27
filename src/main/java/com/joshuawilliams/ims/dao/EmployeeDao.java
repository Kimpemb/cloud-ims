// EmployeeDao.java
package com.joshuawilliams.ims.dao;

import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.model.Employee;
import com.joshuawilliams.ims.utils.PasswordUtils;
import com.joshuawilliams.ims.utils.UIHelper;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDao {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeDao.class);
    private final Connection connection;

    public EmployeeDao(Connection connection) {
        this.connection = connection;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve employees.", e);
        }
        return employees;
    }

    public Optional<Employee> getEmployeeById(String id) {
        String sql = "SELECT * FROM employees WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmployee(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve employee with ID: {}", id, e);
        }
        return Optional.empty();
    }

    public boolean insertEmployee(Employee employee) {
        String sql = """
                INSERT INTO employees (name, department_id, role_id, email, salary, date_of_birth, hire_date,
                address, manager_id, phone_number, performance_review, emergency_contact, national_id, status,
                employment_type, password)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            populateInsertStatement(stmt, employee);
            int rows = stmt.executeUpdate();
            logger.info("Inserted employee [{}], Rows affected: {}", employee.getName(), rows);
            return rows > 0;
        } catch (SQLException e) {
            logger.error("Failed to insert employee [{}]: {}", employee.getName(), e.getMessage(), e);
            return false;
        }
    }

    @Deprecated
    public void addEmployee(Employee employee) {
        if (!insertEmployee(employee)) {
            throw new RuntimeException("Could not add employee.");
        }
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
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
                rs.getString("password")
        );
    }

    private void populateInsertStatement(PreparedStatement stmt, Employee emp) throws SQLException {
        stmt.setString(1, emp.getName());
        stmt.setString(2, emp.getDepartment());
        stmt.setString(3, emp.getRole());
        stmt.setString(4, emp.getEmail());
        stmt.setDouble(5, emp.getSalary());
        stmt.setDate(6, emp.getDateOfBirth() != null ? new Date(emp.getDateOfBirth().getTime()) : null);
        stmt.setDate(7, emp.getHireDate() != null ? new Date(emp.getHireDate().getTime()) : null);
        stmt.setString(8, emp.getAddress());
        stmt.setString(9, emp.getManagerId());
        stmt.setString(10, emp.getPhoneNumber());
        stmt.setString(11, emp.getPerformanceReview());
        stmt.setString(12, emp.getEmergencyContact());
        stmt.setString(13, emp.getNationalId());
        stmt.setString(14, emp.getStatus());
        stmt.setString(15, emp.getEmploymentType());
        stmt.setString(16, emp.getPassword());
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

    public Optional<Employee> getEmployeeByEmail(String email) {
        String query = "SELECT * FROM employees WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setEmail(rs.getString("email"));  // Using String for email
                    employee.setPassword(rs.getString("password"));  // Using String for password
                    // Set other fields similarly...
                    return Optional.of(employee);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching employee by email: {}", email, e);
        }
        return Optional.empty();
    }


    // EmployeeDao.java
    public int getTotalEmployees() {
        String query = "SELECT COUNT(*) FROM employees";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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

    public void ensureDefaultAdminExists() throws SQLException {
        String adminRoleQuery = "SELECT id FROM roles WHERE LOWER(role_name) = 'admin' LIMIT 1";
        String createAdminRoleQuery = "INSERT INTO roles (role_name) VALUES ('Admin')";
        String adminCheckQuery = "SELECT * FROM employees WHERE role_id = ? LIMIT 1";
        String createAdminQuery = "INSERT INTO employees (name, email, password, role_id, status, is_default_password) " +
                "VALUES ('System Admin', 'admin@system.com', ?, ?, 'Active', TRUE)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkRoleStmt = connection.prepareStatement(adminRoleQuery);
             PreparedStatement createRoleStmt = connection.prepareStatement(createAdminRoleQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement checkAdminStmt = connection.prepareStatement(adminCheckQuery);
             PreparedStatement createAdminStmt = connection.prepareStatement(createAdminQuery)) {

            // 1. Check or Create "Admin" Role
            int adminRoleId;
            ResultSet roleResultSet = checkRoleStmt.executeQuery();

            if (roleResultSet.next()) {
                adminRoleId = roleResultSet.getInt("id");
            } else {
                createRoleStmt.executeUpdate();
                ResultSet generatedKeys = createRoleStmt.getGeneratedKeys();
                adminRoleId = generatedKeys.next() ? generatedKeys.getInt(1) : -1;

                if (adminRoleId == -1) {
                    throw new SQLException("Failed to create 'Admin' role, no ID obtained.");
                }
            }

            // 2. Check if Default Admin Exists
            checkAdminStmt.setInt(1, adminRoleId);
            ResultSet adminResultSet = checkAdminStmt.executeQuery();

            if (!adminResultSet.next()) {
                // 3. Create Default Admin Account
                String hashedPassword = PasswordUtils.hashPassword("Admin@1234");
                createAdminStmt.setString(1, hashedPassword);
                createAdminStmt.setInt(2, adminRoleId);
                createAdminStmt.executeUpdate();

                System.out.println("Default admin created with email: admin@system.com and password: Admin@1234");
            }
        }
    }


    // Method to update the password by email
    public boolean updatePasswordByEmail(String email, String hashedPassword) throws SQLException {
        String query = "UPDATE employees SET password = ? WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, hashedPassword);
            statement.setString(2, email);
            return statement.executeUpdate() > 0;
        }
    }

    // In EmployeeDao.java
    public boolean isDefaultAdminPassword(String email) throws SQLException {
        String query = "SELECT * FROM employees WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, PasswordUtils.hashPassword("Admin@1234")); // Default password
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next();
        }
    }
}

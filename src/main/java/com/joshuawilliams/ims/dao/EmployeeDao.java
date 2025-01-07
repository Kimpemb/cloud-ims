package com.joshuawilliams.ims.dao;

import com.joshuawilliams.ims.model.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;


public class EmployeeDao {
    private static final Logger logger = Logger.getLogger(EmployeeDao.class.getName());
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
                Employee employee = new Employee(
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
                        rs.getString("national_id")
                );
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    // Add a new employee
    // Add a new employee
    public void addEmployee(Employee employee) {
        String query = "INSERT INTO employees (name, department_id, role_id, email, salary, date_of_birth, hire_date, " +
                "address, manager_id, phone_number, performance_review, emergency_contact, national_id, status, employment_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            logger.info("Parameter 1 (Name): " + employee.getName());

            stmt.setString(2, employee.getDepartment());
            logger.info("Parameter 2 (Department ID): " + employee.getDepartment());

            stmt.setString(3, employee.getRole());
            logger.info("Parameter 3 (Role ID): " + employee.getRole());

            stmt.setString(4, employee.getEmail());
            logger.info("Parameter 4 (Email): " + employee.getEmail());

            stmt.setDouble(5, employee.getSalary());
            logger.info("Parameter 5 (Salary): " + employee.getSalary());

            stmt.setDate(6, employee.getDateOfBirth() != null ? new Date(employee.getDateOfBirth().getTime()) : null);
            logger.info("Parameter 6 (Date of Birth): " + employee.getDateOfBirth());

            stmt.setDate(7, employee.getHireDate() != null ? new Date(employee.getHireDate().getTime()) : null);
            logger.info("Parameter 7 (Hire Date): " + employee.getHireDate());

            stmt.setString(8, employee.getAddress());
            logger.info("Parameter 8 (Address): " + employee.getAddress());

            stmt.setString(9, employee.getManagerId());
            logger.info("Parameter 9 (Manager ID): " + employee.getManagerId());

            stmt.setString(10, employee.getPhoneNumber());
            logger.info("Parameter 10 (Phone Number): " + employee.getPhoneNumber());

            stmt.setString(11, employee.getPerformanceReview());
            logger.info("Parameter 11 (Performance Review): " + employee.getPerformanceReview());

            stmt.setString(12, employee.getEmergencyContact());
            logger.info("Parameter 12 (Emergency Contact): " + employee.getEmergencyContact());

            stmt.setString(13, employee.getNationalId());
            logger.info("Parameter 13 (National ID): " + employee.getNationalId());

            stmt.setString(14, employee.getStatus());
            logger.info("Parameter 14 (Status): " + employee.getStatus());

            stmt.setString(15, employee.getEmploymentType());
            logger.info("Parameter 15 (Employment Type): " + employee.getEmploymentType());

            logger.info("Executing query...");
            stmt.executeUpdate();
            logger.info("Query executed successfully. Employee added.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error occurred while adding a new employee: " + e.getMessage(), e);
            throw new RuntimeException("Error adding employee: " + e.getMessage(), e);
        }
    }



    // Update an existing employee
    // Update an existing employee
    public void updateEmployee(Employee employee) {
        String query = "UPDATE employees SET name = ?, department_id = ?, role_id = ?, email = ?, salary = ?, date_of_birth = ?, " +
                "hire_date = ?, address = ?, manager_id = ?, phone_number = ?, performance_review = ?, emergency_contact = ?, " +
                "national_id = ?, status = ?, employment_type = ? WHERE id = ?";

        logger.info("Initiating the process to update employee with ID: " + employee.getId());

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Log query structure once
            logger.fine("SQL Query: " + query);

            // Set query parameters and log each parameter
            stmt.setString(1, employee.getName());
            logger.fine("Set Parameter 1 (Name): " + employee.getName());

            stmt.setString(2, employee.getDepartment());
            logger.fine("Set Parameter 2 (Department ID): " + employee.getDepartment());

            stmt.setString(3, employee.getRole());
            logger.fine("Set Parameter 3 (Role ID): " + employee.getRole());

            stmt.setString(4, employee.getEmail());
            logger.fine("Set Parameter 4 (Email): " + employee.getEmail());

            stmt.setDouble(5, employee.getSalary());
            logger.fine("Set Parameter 5 (Salary): " + employee.getSalary());

            // Convert java.util.Date to java.sql.Date for Date of Birth
            java.util.Date utilDateOfBirth = employee.getDateOfBirth();  // Get java.util.Date
            java.sql.Date sqlDateOfBirth = (utilDateOfBirth != null) ? new java.sql.Date(utilDateOfBirth.getTime()) : null;
            stmt.setDate(6, sqlDateOfBirth);
            logger.fine("Set Parameter 6 (Date of Birth): " + (utilDateOfBirth != null ? utilDateOfBirth.toString() : "null"));

            // Convert java.util.Date to java.sql.Date for Hire Date
            java.util.Date utilHireDate = employee.getHireDate();  // Get java.util.Date
            java.sql.Date sqlHireDate = (utilHireDate != null) ? new java.sql.Date(utilHireDate.getTime()) : null;
            stmt.setDate(7, sqlHireDate);
            logger.fine("Set Parameter 7 (Hire Date): " + (utilHireDate != null ? utilHireDate.toString() : "null"));

            stmt.setString(8, employee.getAddress());
            logger.fine("Set Parameter 8 (Address): " + employee.getAddress());

            stmt.setString(9, employee.getManagerId());
            logger.fine("Set Parameter 9 (Manager ID): " + employee.getManagerId());

            stmt.setString(10, employee.getPhoneNumber());
            logger.fine("Set Parameter 10 (Phone Number): " + employee.getPhoneNumber());

            stmt.setString(11, employee.getPerformanceReview());
            logger.fine("Set Parameter 11 (Performance Review): " + employee.getPerformanceReview());

            stmt.setString(12, employee.getEmergencyContact());
            logger.fine("Set Parameter 12 (Emergency Contact): " + employee.getEmergencyContact());

            stmt.setString(13, employee.getNationalId());
            logger.fine("Set Parameter 13 (National ID): " + employee.getNationalId());

            stmt.setString(14, employee.getStatus());
            logger.fine("Set Parameter 14 (Status): " + employee.getStatus());

            stmt.setString(15, employee.getEmploymentType());
            logger.fine("Set Parameter 15 (Employment Type): " + employee.getEmploymentType());

            stmt.setString(16, employee.getId()); // Assuming employee ID is a String
            logger.fine("Set Parameter 16 (Employee ID): " + employee.getId());

            // Execute the update
            logger.info("Executing update for employee with ID: " + employee.getId());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("Employee with ID: " + employee.getId() + " updated successfully.");
            } else {
                logger.warning("No employee found with ID: " + employee.getId());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error occurred while updating employee with ID: " + employee.getId(), e);
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
                logger.info("Employee with ID: " + employeeId + " deleted successfully.");
            } else {
                logger.warning("No employee found with ID: " + employeeId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error occurred while deleting employee with ID: " + employeeId, e);
        }
    }


    // Add a new department
    public void addDepartment(String departmentName) {
        String query = "INSERT INTO departments (name) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, departmentName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a new role
    public void addRole(String roleName) {
        String query = "INSERT INTO roles (name) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roleName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

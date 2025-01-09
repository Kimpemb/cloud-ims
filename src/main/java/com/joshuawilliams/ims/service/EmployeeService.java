package com.joshuawilliams.ims.service;

import com.joshuawilliams.ims.dao.EmployeeDao;
import com.joshuawilliams.ims.model.Employee;
import com.joshuawilliams.ims.utils.EmailValidator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;


import static com.joshuawilliams.ims.dao.EmployeeDao.logger;

public class EmployeeService {
    private final EmployeeDao employeeDao;
    private Connection connection;

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public List<Employee> getAllEmployees() {
        return employeeDao.getAllEmployees();
    }



    public boolean addEmployee(Employee employee) {
        try {
            // Validate email before proceeding
            if (!EmailValidator.isValidEmail(employee.getEmail())) {
                throw new IllegalArgumentException("Invalid email address.");
            }

            // Validate Date of Birth
            if (!isValidDateOfBirth(employee.getDateOfBirth())) {
                throw new IllegalArgumentException("Employee must be at least 18 years old.");
            }

            // Validate Hire Date
            if (!isValidHireDate(employee.getHireDate())) {
                throw new IllegalArgumentException("Hire date cannot be in the future.");
            }

            // Business logic before adding an employee, if any
            employeeDao.addEmployee(employee);
            return true; // Employee added successfully
        } catch (IllegalArgumentException e) {
            logger.error("Error adding employee: {}", e.getMessage(), e); // Log the error
            return false;
        }
    }



    public boolean updateEmployee(Employee employee) {
        try {
            // Business logic before updating an employee, if any
            employeeDao.updateEmployee(employee);
            return true; // Employee updated successfully
        } catch (Exception e) {
            logger.error("Error updating employee: {}", e.getMessage(), e); // Log the error
            return false;
        }
    }

    public boolean deleteEmployee(String employeeId) {
        try {
            // Business logic before deleting an employee, if any
            employeeDao.deleteEmployee(employeeId);
            return true; // Employee deleted successfully
        } catch (Exception e) {
            logger.error("Error deleting employee: {}", e.getMessage(), e); // Log the error
            return false;
        }
    }

    public boolean addDepartment(String name, String code, String description, String managerName, String email, String location, String status) {
        try {
            // Logic to add the department (call the EmployeeDao method)
            employeeDao.addDepartment(name, code, description, managerName, email, location, status);
            return true; // Department added successfully
        } catch (Exception e) {
            logger.error("Error adding department: {}", e.getMessage(), e); // Log the error
            return false;
        }
    }


    public boolean addRole(String roleName) {
        try {
            // Logic to add the role (e.g., update the database or local list)
            employeeDao.addRole(roleName);
            return true; // Role added successfully
        } catch (Exception e) {
            logger.error("Error adding role: {}", e.getMessage(), e); // Log the error
            return false;
        }
    }


    // Inside EmployeeDao or a utility class
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getString("id"));
        employee.setName(rs.getString("name"));
        employee.setDepartment(rs.getString("department_id"));
        employee.setRole(rs.getString("role_id"));
        employee.setEmail(rs.getString("email"));
        employee.setSalary(rs.getDouble("salary"));
        employee.setDateOfBirth(rs.getDate("date_of_birth"));
        employee.setHireDate(rs.getDate("hire_date"));
        employee.setAddress(rs.getString("address"));
        employee.setManagerId(rs.getString("manager_id"));
        employee.setPhoneNumber(rs.getString("phone_number"));
        employee.setPerformanceReview(rs.getString("performance_review"));
        employee.setEmergencyContact(rs.getString("emergency_contact"));
        employee.setNationalId(rs.getString("national_id"));
        employee.setStatus(rs.getString("status"));
        employee.setEmploymentType(rs.getString("employment_type"));
        return employee;
    }


    private boolean isValidHireDate(Date hireDate) {
        if (hireDate == null) {
            return false; // Hire date is required
        }

        Date currentDate = new Date();
        return !hireDate.after(currentDate); // Hire date should not be after the current date
    }

    private boolean isValidDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth == null) {
            return false; // Date of birth is required
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18); // Subtract 18 years from the current date
        Date thresholdDate = calendar.getTime();

        return dateOfBirth.before(thresholdDate); // Date of birth should be before the threshold date
    }
}

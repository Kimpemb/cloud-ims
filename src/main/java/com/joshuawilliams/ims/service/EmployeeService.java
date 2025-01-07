package com.joshuawilliams.ims.service;


import com.joshuawilliams.ims.dao.EmployeeDao;
import com.joshuawilliams.ims.model.Employee;
import com.joshuawilliams.ims.utils.EmailValidator;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class EmployeeService {
    private EmployeeDao employeeDao;

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public List<Employee> getAllEmployees() {
        return employeeDao.getAllEmployees();
    }




    public void addDepartment(String departmentName) {
        // Logic to add the department (e.g., update the database or local list)
        employeeDao.addDepartment(departmentName);
    }

    public void addRole(String roleName) {
        // Logic to add the role (e.g., update the database or local list)
        employeeDao.addRole(roleName);
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


    // EmployeeService
    public void addEmployee(Employee employee) {
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
    }


    public void updateEmployee(Employee employee) throws SQLException {
        // Business logic before updating an employee, if any
        employeeDao.updateEmployee(employee);
    }

    public void deleteEmployee(String employeeId) {
        // Business logic before deleting an employee, if any
        employeeDao.deleteEmployee(employeeId);
    }
}

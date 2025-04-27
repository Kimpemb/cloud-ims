// File: src/main/java/com/joshuawilliams/ims/service/EmployeeService.java
package com.joshuawilliams.ims.service;

import com.joshuawilliams.ims.dao.EmployeeDao;
import com.joshuawilliams.ims.model.Employee;
import com.joshuawilliams.ims.utils.EmailValidator;
import com.joshuawilliams.ims.utils.PasswordUtils;
import com.joshuawilliams.ims.utils.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class EmployeeService {

    private final EmployeeDao employeeDao;
    private final Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    public EmployeeService(EmployeeDao employeeDao, Connection connection) {
        this.employeeDao = employeeDao;
        this.connection = connection;
    }

    public List<Employee> getAllEmployees() {
        return employeeDao.getAllEmployees();
    }

    public ServiceResult<Void> addEmployee(Employee employee) {
        try {
            if (!validateEmployee(employee, true)) {
                return new ServiceResult<>(false, "Invalid employee data.", null);
            }

            employee.setPassword(PasswordUtils.hashPassword(employee.getPassword()));
            insertEmployee(employee);
            logger.info("Employee added: {}", employee.getEmail());
            return new ServiceResult<>(true, "Employee added successfully.", null);
        } catch (Exception e) {
            logger.error("Failed to add employee: {}", e.getMessage(), e);
            return new ServiceResult<>(false, "Error while adding employee.", null);
        }
    }

    public ServiceResult<Void> updateEmployee(Employee employee) {
        try {
            if (!validateEmployee(employee, false)) {
                return new ServiceResult<>(false, "Invalid employee data.", null);
            }

            if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
                if (!PasswordUtils.isHashed(employee.getPassword())) {
                    employee.setPassword(PasswordUtils.hashPassword(employee.getPassword()));
                }
            } else {
                employeeDao.getEmployeeById(employee.getId())
                        .ifPresent(existing -> employee.setPassword(existing.getPassword()));
            }

            employeeDao.updateEmployee(employee);
            logger.info("Employee updated: {}", employee.getId());
            return new ServiceResult<>(true, "Employee updated successfully.", null);
        } catch (Exception e) {
            logger.error("Failed to update employee: {}", e.getMessage(), e);
            return new ServiceResult<>(false, "Error while updating employee.", null);
        }
    }

    public ServiceResult<Void> deleteEmployee(String employeeId) {
        try {
            employeeDao.deleteEmployee(employeeId);
            logger.info("Employee deleted: {}", employeeId);
            return new ServiceResult<>(true, "Employee deleted successfully.", null);
        } catch (Exception e) {
            logger.error("Failed to delete employee: {}", e.getMessage(), e);
            return new ServiceResult<>(false, "Error while deleting employee.", null);
        }
    }

    public Optional<Employee> login(String email, String password) throws LoginException {
        try {
            Optional<Employee> optionalEmployee = employeeDao.getEmployeeByEmail(email);
            if (optionalEmployee.isEmpty()) {
                throw new LoginException("No employee found with email: " + email);
            }

            Employee employee = optionalEmployee.get();
            if (!PasswordUtils.verifyPassword(password, employee.getPassword())) {
                throw new LoginException("Invalid password for email: " + email);
            }

            return Optional.of(employee);
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage(), e);
            throw new LoginException("Error during login. Please try again.");
        }
    }

    public boolean isDefaultAdminPassword(String email) throws SQLException {
        if (!"admin@system.com".equalsIgnoreCase(email)) {
            return false;
        }
        return employeeDao.isDefaultAdminPassword(email);
    }

    public boolean updatePasswordByEmail(String email, String newPassword) throws SQLException {
        String hashedPassword = PasswordUtils.isHashed(newPassword) ? newPassword : PasswordUtils.hashPassword(newPassword);
        return employeeDao.updatePasswordByEmail(email, hashedPassword);
    }

    public ServiceResult<Void> addDepartment(String name, String code, String description,
                                             String managerName, String email, String location, String status) {
        try {
            employeeDao.addDepartment(name, code, description, managerName, email, location, status);
            logger.info("Department added: {}", name);
            return new ServiceResult<>(true, "Department added successfully.", null);
        } catch (Exception e) {
            logger.error("Failed to add department: {}", e.getMessage(), e);
            return new ServiceResult<>(false, "Error while adding department.", null);
        }
    }

    public ServiceResult<Void> addRole(String roleName) {
        try {
            employeeDao.addRole(roleName);
            logger.info("Role added: {}", roleName);
            return new ServiceResult<>(true, "Role added successfully.", null);
        } catch (Exception e) {
            logger.error("Failed to add role: {}", e.getMessage(), e);
            return new ServiceResult<>(false, "Error while adding role.", null);
        }
    }

    public int getTotalEmployees() {
        return employeeDao.getTotalEmployees();
    }

    private boolean validateEmployee(Employee employee, boolean checkPassword) {
        if (!EmailValidator.isValidEmail(employee.getEmail())) {
            return false;
        }
        if (!isValidDateOfBirth(employee.getDateOfBirth())) {
            return false;
        }
        if (!isValidHireDate(employee.getHireDate())) {
            return false;
        }
        if (checkPassword && (employee.getPassword() == null || !isValidPassword(employee.getPassword()))) {
            return false;
        }
        return true;
    }

    public boolean isValidPassword(String password) {
        if (PasswordUtils.isHashed(password)) {
            return true;
        }
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";
        return password.matches(pattern);
    }

    public boolean isValidHireDate(java.util.Date hireDate) {
        if (hireDate == null) return false;
        return !hireDate.after(new java.util.Date());
    }

    public boolean isValidDateOfBirth(java.util.Date dob) {
        if (dob == null) return false;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -18);
        return dob.before(cal.getTime());
    }

    // ============================ INSERT EMPLOYEE METHOD ============================

    public void insertEmployee(Employee employee) {
        String sql = "INSERT INTO employees (name, department_id, role_id, email, salary, date_of_birth, hire_date, " +
                "address, manager_id, phone_number, performance_review, emergency_contact, national_id, status, " +
                "employment_type, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
            stmt.setString(16, employee.getPassword());

            stmt.executeUpdate();
            logger.info("Employee inserted successfully: {}", employee.getEmail());
        } catch (SQLException e) {
            logger.error("Error inserting employee: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to insert employee.", e);
        }
    }
}

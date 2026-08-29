package com.joshuawilliams.ims.controller;

import com.joshuawilliams.ims.dao.EmployeeDao;
import com.joshuawilliams.ims.model.Employee;
import com.joshuawilliams.ims.service.EmployeeService;
import com.joshuawilliams.ims.utils.LoginManager;

import java.sql.Connection;
import java.util.Optional;
import java.util.Scanner;

public class LoginController {

    private EmployeeService employeeService;

    public LoginController(EmployeeDao employeeDao, Connection connection) {
        this.employeeService = new EmployeeService(employeeDao, connection);
    }

    public void handleLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Email and password cannot be empty.");
            return;
        }

        try {
            Optional<Employee> employee = employeeService.login(email, password);

            if (employee.isPresent()) {
                System.out.println("Login successful: " + employee.get().getName());
                LoginManager.setLoggedInEmployee(employee.get());  // Use LoginManager to manage login session
                // Proceed with the next action (e.g., navigate to the dashboard)
            } else {
                System.out.println("Invalid email or password.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during login. Please try again.");
            e.printStackTrace();
        }
    }
}

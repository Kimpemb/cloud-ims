package com.joshuawilliams.ims.utils;

import com.joshuawilliams.ims.model.Employee;

public class SessionManager {
    private static SessionManager instance;
    private Employee loggedInEmployee;

    // Private constructor to prevent instantiation
    private SessionManager() {}

    // Get singleton instance
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Set the logged-in employee
    public void setLoggedInEmployee(Employee employee) {
        loggedInEmployee = employee;
    }

    // Get the logged-in employee
    public Employee getLoggedInEmployee() {
        return loggedInEmployee;
    }

    // Clear the session (e.g., on logout)
    public void clearSession() {
        loggedInEmployee = null;
    }

    // Get username safely, with fallback
    public String getCurrentUsernameOrDefault() {
        if (loggedInEmployee != null && loggedInEmployee.getName() != null && !loggedInEmployee.getName().isEmpty()) {
            return loggedInEmployee.getName();
        }
        return "Admin"; // Default username if no employee is logged in
    }

    // Check if an employee is logged in
    public boolean isLoggedIn() {
        return loggedInEmployee != null;
    }
}
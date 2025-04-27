package com.joshuawilliams.ims.utils;

import com.joshuawilliams.ims.model.Employee;

public class SessionManager {

    private static Employee loggedInEmployee;

    // Private constructor to prevent instantiation
    public SessionManager() {}

    // Set the logged-in employee
    public static void setLoggedInEmployee(Employee employee) {
        loggedInEmployee = employee;
    }

    // Get the logged-in employee
    public static Employee getLoggedInEmployee() {
        return loggedInEmployee;
    }

    // Clear the session (e.g., on logout)
    public static void clearSession() {
        loggedInEmployee = null;
    }

    // Get username safely, with fallback
    public static String getCurrentUsernameOrDefault() {
        if (loggedInEmployee != null && loggedInEmployee.getName() != null && !loggedInEmployee.getName().isEmpty()) {
            return loggedInEmployee.getName();
        }
        return "Admin"; // Default username if no employee is logged in
    }

    // Check if an employee is logged in
    public static boolean isLoggedIn() {
        return loggedInEmployee != null;
    }
}

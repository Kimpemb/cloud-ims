package com.joshuawilliams.ims.utils;

import com.joshuawilliams.ims.model.Employee;

public class SessionManager {

    private static Employee loggedInEmployee;

    // Private constructor to prevent instantiation
    private SessionManager() {}

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
}

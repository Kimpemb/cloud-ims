package com.joshuawilliams.ims.utils;

import com.joshuawilliams.ims.model.Employee;

public class LoginManager {
    private static Employee loggedInEmployee;

    // Sets the logged-in employee
    public static void setLoggedInEmployee(Employee employee) {
        loggedInEmployee = employee;
    }

    // Gets the currently logged-in employee
    public static Employee getLoggedInEmployee() {
        return loggedInEmployee;
    }

    // Clears the logged-in employee (on logout)
    public static void logout() {
        loggedInEmployee = null;
    }
}

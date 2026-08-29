package com.joshuawilliams.ims.utils;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class EmailValidator {

    // Regular expression for email validation
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Method to validate email
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isValidHireDate(LocalDate hireDate) {
        if (hireDate == null) {
            return false;
        }

        // Hire date should not be in the future
        return !hireDate.isAfter(LocalDate.now());
    }

    public boolean isValidDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return false;
        }

        // Calculate the age of the employee
        Period period = Period.between(dateOfBirth, LocalDate.now());

        // Employee must be at least 18 years old
        return period.getYears() >= 18;
    }
}

package com.joshuawilliams.ims.dao;

import com.joshuawilliams.ims.model.Customer;
import com.joshuawilliams.ims.model.Status;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerDao {

    private static final Logger logger = Logger.getLogger(CustomerDao.class.getName());
    private Connection connection;

    // Constructor for database connection
    public CustomerDao(Connection connection) {
        this.connection = connection;
    }

    // Generate Customer ID (e.g., CUST-YYYYMMDD-###)
    private String generateCustomerId() throws SQLException {
        String datePart = LocalDate.now().toString().replace("-", "");
        int count = getCountForDate(datePart);  // Get the current count for today
        String customerId;

        // Try generating a unique ID, retry if it already exists
        do {
            count++;  // Increment the count
            customerId = "CUST-" + datePart + "-" + String.format("%03d", count);
        } while (isCustomerIdExists(customerId));  // Check if the ID exists

        return customerId;
    }

    // Check if customer ID already exists in the database
    private boolean isCustomerIdExists(String customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE customer_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.getInt(1) > 0;  // Return true if customer ID exists
        }
    }

    public Customer getCustomerById(String customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);  // Set the customer ID parameter
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Map the result set to a Customer object
                return mapResultSetToCustomer(resultSet);
            } else {
                return null;  // Return null if no customer with the given ID is found
            }
        }
    }


    public boolean createCustomer(Customer customer) throws SQLException {
        String customerId = generateCustomerId();  // Generate Customer ID

        // Convert the status to match the database case (e.g., "Active", "Inactive")
        String status = customer.getStatus().toString().substring(0, 1).toUpperCase() + customer.getStatus().toString().substring(1).toLowerCase();

        // Validate customer status before inserting into the database
        if (!isValidStatus(customer.getStatus())) {
            logger.log(Level.SEVERE, "Invalid status: " + customer.getStatus());
            return false;  // Return false if status is invalid
        }

        // Check if the email already exists in the database
        if (emailExists(customer.getEmail())) {
            logger.log(Level.WARNING, "Email already in use: " + customer.getEmail());
            throw new SQLException("Email is already in use. Please choose a different email.");
        }

        String sql = "INSERT INTO customers (customer_id, first_name, last_name, email, phone_number, address, " +
                "date_of_birth, status, registration_date, loyalty_points, loyalty_level, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set generated customer ID and map customer properties to statement
            statement.setString(1, customerId);
            mapCustomerToStatement(customer, statement);

            // Set status to the correct case
            statement.setString(8, status);

            // Execute the insert and return true if successful
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            // Log error with clear message
            logger.log(Level.SEVERE, "Error creating customer with ID: " + customerId, e);
            throw new SQLException("Error inserting customer into the database", e);  // Re-throw with descriptive message
        }
    }

    private boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM customers WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.getInt(1) > 0;  // Return true if email exists
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if email exists: " + email, e);
            throw new SQLException("Error checking email existence", e);
        }
    }


    // Helper method to validate status
    private boolean isValidStatus(Status status) {
        return status == Status.Active || status == Status.Inactive;
    }


    // Update customer details

    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, phone_number = ?, address = ?, " +
                "date_of_birth = ?, status = ?, registration_date = ?, loyalty_points = ?, loyalty_level = ?, notes = ? " +
                "WHERE customer_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Validate the status field
            String status = customer.getStatus().toString();
            if (!status.equals("Active") && !status.equals("Inactive")) {
                throw new IllegalArgumentException("Invalid status value: " + status);
            }

            // Set parameters for the statement
            statement.setString(1, customer.getFirstName());
            statement.setString(2, customer.getLastName());
            statement.setString(3, customer.getEmail());
            statement.setString(4, customer.getPhoneNumber());
            statement.setString(5, customer.getAddress());
            statement.setDate(6, Date.valueOf(customer.getDateOfBirth()));
            statement.setString(7, status);  // Set the validated status
            statement.setDate(8, Date.valueOf(customer.getRegistrationDate()));
            statement.setInt(9, customer.getLoyaltyPoints());
            statement.setString(10, customer.getLoyaltyLevel());
            statement.setString(11, customer.getNotes());
            statement.setString(12, customer.getCustomerId());

            // Execute the update and return true if successful
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating customer with ID: " + customer.getCustomerId(), e);
            throw e;  // Rethrow the exception to be handled at a higher level
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Error: " + e.getMessage(), e);
            throw e;  // Rethrow the exception to be handled at a higher level
        }
    }


    // In CustomerDao.java
    public boolean saveCustomer(Customer customer) throws SQLException {
        return createCustomer(customer);  // Reusing createCustomer method logic
    }


    // Delete customer by ID
    public boolean deleteCustomer(String customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);  // Use setString to pass customer_id as TEXT
            return statement.executeUpdate() > 0;
        }
    }


    // Get all customers
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                customers.add(mapResultSetToCustomer(resultSet));
            }
        }
        return customers;
    }

    // Helper method to map ResultSet to Customer object
    private Customer mapResultSetToCustomer(ResultSet resultSet) throws SQLException {
        String customerId = resultSet.getString("customer_id");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        String email = resultSet.getString("email");
        String phone = resultSet.getString("phone_number");
        String address = resultSet.getString("address");
        LocalDate dob = resultSet.getDate("date_of_birth").toLocalDate();
        String statusString = resultSet.getString("status");
        Status status = mapResultSetToStatus(statusString);  // Call the method to map status
        LocalDate registrationDate = resultSet.getDate("registration_date").toLocalDate();
        int loyaltyPoints = resultSet.getInt("loyalty_points");
        String loyaltyLevel = resultSet.getString("loyalty_level");
        String notes = resultSet.getString("notes");

        return new Customer(customerId, firstName, lastName, email, phone, address, dob, status, registrationDate, loyaltyPoints, loyaltyLevel, notes);
    }

    private Status mapResultSetToStatus(String statusString) {
        try {
            return Status.valueOf(statusString.toUpperCase());  // Normalize the string to uppercase and convert to Status enum
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status value: " + statusString);
            return Status.Inactive;  // Default to INACTIVE or handle as needed
        }
    }


    // Helper method to map Customer object to PreparedStatement
    private void mapCustomerToStatement(Customer customer, PreparedStatement statement) throws SQLException {
        statement.setString(2, customer.getFirstName());
        statement.setString(3, customer.getLastName());
        statement.setString(4, customer.getEmail());
        statement.setString(5, customer.getPhoneNumber());
        statement.setString(6, customer.getAddress());
        statement.setDate(7, Date.valueOf(customer.getDateOfBirth()));
        statement.setString(8, customer.getStatus().toString());  // Assuming Status is an Enum
        statement.setDate(9, Date.valueOf(customer.getRegistrationDate()));
        statement.setInt(10, customer.getLoyaltyPoints());
        statement.setString(11, customer.getLoyaltyLevel());
        statement.setString(12, customer.getNotes());
    }

    // Get count of customers created on a given date
    public int getCountForDate(String datePart) {
        String query = "SELECT COUNT(*) FROM customers WHERE customer_id LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "CUST-" + datePart + "%");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);  // Return the count of customers created today
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting customer count for date: " + datePart, e);
        }
        return 0;  // Default if no customers found
    }

}

package com.joshuawilliams.ims.service;

import com.joshuawilliams.ims.dao.CustomerDao;
import com.joshuawilliams.ims.model.Customer;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerService {

    private final CustomerDao customerDao;
    private static final Logger logger = Logger.getLogger(CustomerService.class.getName());

    // Constructor for dependency injection of the DAO
    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    // Method to create a new customer
    public boolean createCustomer(Customer customer) throws SQLException {
        if (validateCustomerAge(customer.getDateOfBirth())) {
            String customerId = generateCustomerId();  // Generate Customer ID
            customer.setCustomerId(customerId);  // Set the customer ID to the customer object

            // Delegate to DAO to save customer (DAO will handle DB interaction)
            return customerDao.saveCustomer(customer);
        } else {
            logger.log(Level.WARNING, "Customer age is invalid.");
            return false;
        }
    }

    // Method to fetch customer by ID
    public Customer getCustomerById(String id) throws SQLException {
        return customerDao.getCustomerById(id);  // Get customer by ID from DAO
    }

    // Method to fetch all customers
    public List<Customer> getAllCustomers() throws SQLException {
        return customerDao.getAllCustomers();  // Get all customers from DAO
    }

    // Method to update a customer's information
    public boolean updateCustomer(Customer customer) throws SQLException {
        // Delegate the update operation to the DAO
        return customerDao.updateCustomer(customer);
    }

    // Method to delete a customer by ID
    public boolean deleteCustomer(int customerId) throws SQLException {
        return customerDao.deleteCustomer(customerId);  // Delegate to DAO
    }



    // Validate the customer's age (between 13 and 99)
    private boolean validateCustomerAge(LocalDate dob) {
        int age = LocalDate.now().getYear() - dob.getYear();
        if (dob.getMonthValue() > LocalDate.now().getMonthValue()) {
            age--;
        }
        return age >= 13 && age <= 99;
    }

    // Method to generate a customer ID
    public String generateCustomerId() {
        // Get current date in format YYYYMMDD
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Generate a sequence number based on the current date
        int sequenceNumber = customerDao.getCountForDate(datePart) + 1;  // Assuming getCountForDate() gets the number of customers for today

        // Format sequence number to be 3 digits (e.g., 001, 002, 010)
        String sequencePart = String.format("%03d", sequenceNumber);

        // Combine both parts
        return "CUST-" + datePart + "-" + sequencePart;
    }

    // Helper method to convert an int to formatted String ID for deletion
    private String generateCustomerIdFromInt(int id) {
        // Get current date in format YYYYMMDD
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Format the ID to follow the pattern (CUST-YYYYMMDD-###)
        String sequencePart = String.format("%03d", id);
        return "CUST-" + datePart + "-" + sequencePart;
    }
}

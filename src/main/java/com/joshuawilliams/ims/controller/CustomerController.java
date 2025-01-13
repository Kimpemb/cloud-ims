package com.joshuawilliams.ims.controller;

import com.joshuawilliams.ims.dao.CustomerDao;
import com.joshuawilliams.ims.model.Customer;
import com.joshuawilliams.ims.service.CustomerService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerController {
    private static final Logger logger = Logger.getLogger(CustomerController.class.getName());
    private final CustomerService customerService;

    // Constructor to inject the CustomerService dependency
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Create a new customer
    public boolean createCustomer(Customer customer) {
        try {
            return customerService.createCustomer(customer);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating customer", e);
            return false; // Return false on failure
        }
    }

    // Get a customer by their unique ID
    public Customer getCustomerById(String customerId) {
        try {
            return customerService.getCustomerById(customerId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving customer with ID: " + customerId, e);
            return null; // Return null if an error occurs
        }
    }

    // Update an existing customer's information
    public boolean updateCustomer(Customer customer) {
        try {
            return customerService.updateCustomer(customer);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating customer: " + customer.getCustomerId(), e);
            return false; // Return false if update fails
        }
    }

    // Delete a customer by their unique ID
    public boolean deleteCustomer(String customerId) {
        try {
            int id = Integer.parseInt(customerId); // Convert customerId to int
            return customerService.deleteCustomer(id);  // Call deleteCustomer with int parameter
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting customer with ID: " + customerId, e);
            return false; // Return false if deletion fails
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid customer ID format: " + customerId, e);
            return false; // Return false if ID parsing fails
        }
    }

    // Get a list of all customers
    public List<Customer> getAllCustomers() {
        try {
            return customerService.getAllCustomers();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving all customers", e);
            return new ArrayList<>(); // Return empty list in case of failure
        }
    }
}

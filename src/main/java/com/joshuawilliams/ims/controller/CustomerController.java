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
            logger.log(Level.SEVERE, "Error updating customer with ID: " + customer.getCustomerId(), e);
            return false;
        }
    }


    // Delete a customer by their unique ID
    public boolean deleteCustomer(String customerId) {
        try {
            return customerService.deleteCustomer(customerId);  // Pass the customerId directly as a String
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting customer with ID: " + customerId, e);
            return false; // Return false if deletion fails
        }
    }



    // Get a list of all customers
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }
}

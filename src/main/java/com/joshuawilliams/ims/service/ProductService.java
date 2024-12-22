package com.joshuawilliams.ims.service;

import com.joshuawilliams.ims.dao.ProductDao;
import com.joshuawilliams.ims.model.Product;

import java.sql.Connection;

public class ProductService {

    private ProductDao productDao;


    // Constructor with only connection
    public ProductService(Connection connection) {
        this.productDao = new ProductDao(connection);
    }


    // Method to add a product
    public boolean addProduct(String name, double price, int quantity, int categoryId) {
        // Basic validation
        if (name == null || name.trim().isEmpty() || price <= 0 || quantity <= 0 || categoryId <= 0) {
            System.out.println("Invalid input data.");
            return false;
        }

        // Check if the product already exists
        if (productDao.doesProductExist(name)) {
            System.out.println("Product already exists: " + name);
            return false;  // Return false if the product already exists
        }

        // Create Product object
        Product product = new Product(name, price, quantity, categoryId);

        // Call the DAO method to add the product
        return productDao.addProduct(product);
    }

    // Step 1: Implement doesProductExist method in ProductService
    public boolean doesProductExist(String name) {
        // Input validation: Ensure product name is not null or empty
        if (name == null || name.trim().isEmpty()) {
            return false;  // Return false if the product name is invalid
        }

        // Call ProductDao's doesProductExist method to check existence in the database
        return productDao.doesProductExist(name);
    }


}

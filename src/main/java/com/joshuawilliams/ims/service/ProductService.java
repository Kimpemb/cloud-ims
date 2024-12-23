package com.joshuawilliams.ims.service;

import com.joshuawilliams.ims.dao.ProductDao;
import com.joshuawilliams.ims.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductService {

    private ProductDao productDao;
    private Connection connection;  // Store the connection as an instance variable

    // Constructor with only connection
    public ProductService(Connection connection) {
        this.connection = connection;  // Initialize the connection
        this.productDao = new ProductDao(connection);  // Initialize the ProductDao
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

    // Method to fetch all products from the database
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";  // SQL query to fetch all products

        // Use the connection passed to the ProductService class
        try (PreparedStatement statement = connection.prepareStatement(query);  // Use the instance connection
             ResultSet resultSet = statement.executeQuery()) {  // Execute the query and get results

            while (resultSet.next()) {  // Iterate through each row in the result set
                int id = resultSet.getInt("id");  // Get product ID
                String name = resultSet.getString("name");  // Get product name
                double price = resultSet.getDouble("price");  // Get product price
                int quantity = resultSet.getInt("quantity");  // Get product quantity
                int categoryId = resultSet.getInt("category_id");  // Get product category ID

                // Add the product to the list
                products.add(new Product(id, name, price, quantity, categoryId));
            }
        } catch (Exception e) {
            e.printStackTrace();  // Handle any exceptions
        }

        return products;  // Return the list of products
    }
}

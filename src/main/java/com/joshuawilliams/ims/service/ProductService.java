package com.joshuawilliams.ims.service;

import com.joshuawilliams.ims.dao.ProductDao;
import com.joshuawilliams.ims.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductService {

    private ProductDao productDao;
    private Connection connection;

    // Constructor that accepts a connection and initializes ProductDao
    public ProductService(Connection connection) {
        this.connection = connection; // Store the connection
        this.productDao = new ProductDao(connection); // Initialize the ProductDao with the connection
    }

    // Method to add a product
    public boolean addProduct(String name, double price, int quantity, int categoryId) {
        // Validate input data
        if (name == null || name.trim().isEmpty() || price <= 0 || quantity <= 0 || categoryId <= 0) {
            System.out.println("Invalid input data.");
            return false;
        }

        // Check if the product already exists
        if (doesProductExist(name)) {
            System.out.println("Product already exists: " + name);
            return false;
        }

        // Create Product object
        Product product = new Product(name, price, quantity, categoryId);

        // Call the DAO method to add the product to the database
        boolean added = productDao.addProduct(product);
        if (added) {
            System.out.println("Product added successfully: " + name);
        } else {
            System.out.println("Failed to add the product: " + name);
        }

        return added;
    }


    // Method to check if a product exists by name
    public boolean doesProductExist(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false; // Invalid product name
        }
        return productDao.doesProductExist(name); // Check via ProductDao
    }

    // Method to fetch all products from the database
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products"; // SQL query to fetch all products

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Iterate through result set and create Product objects
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                int categoryId = resultSet.getInt("category_id");

                // Add the product to the list
                products.add(new Product(id, name, price, quantity, categoryId));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }

        return products; // Return the list of products
    }

    // Method to update a product in the database
    public boolean updateProduct(Product product) {
        // Validate if the product already exists before updating
        if (doesProductExist(product.getName())) {
            System.out.println("Product name already exists: " + product.getName());
            return false; // If product already exists, return false
        }

        // SQL query to update product details
        String sql = "UPDATE products SET name = ?, price = ?, quantity = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getQuantity());
            stmt.setInt(4, product.getId());

            // Execute update query and check if any rows were affected
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if rows were updated
        } catch (SQLException e) {
            e.printStackTrace(); // Log any SQL exceptions
            return false; // Return false if an error occurred
        }
    }

    // Method to delete a product from the database
    public boolean deleteProduct(Product product) {
        String sql = "DELETE FROM products WHERE id = ?"; // SQL query to delete the product

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, product.getId()); // Set product ID for deletion
            int rowsAffected = stmt.executeUpdate(); // Execute delete query
            return rowsAffected > 0; // Return true if product was deleted
        } catch (SQLException e) {
            e.printStackTrace(); // Log any SQL exceptions
            return false; // Return false if an error occurred
        }
    }
}

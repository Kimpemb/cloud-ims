package com.joshuawilliams.ims.dao;

import com.joshuawilliams.ims.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    private Connection connection;

    // Constructor accepting only connection
    public ProductDao(Connection connection) {
        this.connection = connection;
        try {
            this.connection.setAutoCommit(true);  // Enable autocommit
        } catch (SQLException e) {
            e.printStackTrace();  // Handle exception if autocommit fails
        }
    }

    // Method to add a new product to the database
    public boolean addProduct(Product product) {
        if (product == null || product.getName() == null || product.getName().trim().isEmpty()) {
            System.out.println("Invalid product data.");
            return false;  // Return false if the product or its name is invalid
        }

        String query = "INSERT INTO products (name, price, quantity, category_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setInt(3, product.getQuantity());
            statement.setInt(4, product.getCategoryId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;  // Return true if the product was added successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false if an error occurs
        }
    }

    // Method to check if a product already exists (case-insensitive check)
    public boolean doesProductExist(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;  // Return false if the product name is invalid
        }

        // SQL query with case-insensitive check using LOWER()
        String query = "SELECT COUNT(*) FROM products WHERE LOWER(name) = LOWER(?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                // If a product exists (count > 0), return true
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false if an error occurs
        }
    }

    // Method to search for products by name (case-insensitive search)
    public List<Product> searchProducts(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return new ArrayList<>();  // Return empty list if search query is null or empty
        }

        String query = "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(?)";
        List<Product> products = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + searchQuery + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("category_id")
                );
                products.add(product);  // Add product to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;  // Return the list of products found, or empty if none were found
    }

    // Method to delete a product by its ID
    public boolean deleteProduct(Product product) {
        if (product == null || product.getId() <= 0) {
            System.out.println("Invalid product data.");
            return false;  // Invalid product data
        }

        String query = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, product.getId());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;  // Return true if product was deleted successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false if an error occurs
        }
    }

    // Method to fetch all products from the database
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                int categoryId = resultSet.getInt("category_id");

                products.add(new Product(id, name, price, quantity, categoryId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;  // Return the list of products found
    }

}

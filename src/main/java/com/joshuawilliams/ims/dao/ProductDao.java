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
        String sql = "INSERT INTO products (name, price, quantity, category_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getQuantity());
            stmt.setInt(4, product.getCategoryId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            return false;
        }
    }




    // Method to check if a product already exists (case-insensitive check)
    public boolean doesProductExist(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // Query the database to check for existing product by name
        String query = "SELECT COUNT(*) FROM products WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name.trim()); // Remove leading/trailing spaces before querying
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Product already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // No product found
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
        String query = "SELECT id, name, price, quantity, category_id FROM products";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("category_id")
                );
                products.add(product);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }

        return products;
    }

    // ProductDao.java
    public int getTotalProducts() {
        String query = "SELECT COUNT(*) FROM products";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public Product getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getInt("category_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}

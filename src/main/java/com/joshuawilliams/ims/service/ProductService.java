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
    private Connection connection; // Store the connection as an instance variable

    // Constructor with only connection
    public ProductService(Connection connection) {
        this.connection = connection; // Initialize the connection
        this.productDao = new ProductDao(connection); // Initialize the ProductDao
    }

    // Method to add a product
    public boolean addProduct(String name, double price, int quantity, int categoryId) {
        if (name == null || name.trim().isEmpty() || price <= 0 || quantity <= 0 || categoryId <= 0) {
            System.out.println("Invalid input data.");
            return false;
        }

        // Check if the product already exists
        if (productDao.doesProductExist(name)) {
            System.out.println("Product already exists: " + name);
            return false;
        }

        // Create Product object
        Product product = new Product(name, price, quantity, categoryId);

        // Call the DAO method to add the product
        return productDao.addProduct(product);
    }

    // Method to check if a product exists
    public boolean doesProductExist(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return productDao.doesProductExist(name);
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

        return products;
    }

    // Method to update a product
    public boolean updateProduct(Product product) {
        // Check if the product name already exists
        if (doesProductExist(product.getName())) {
            System.out.println("Product name already exists: " + product.getName());
            return false;  // Return false if the product name already exists
        }

        // Update product logic (execute an SQL UPDATE query)
        String sql = "UPDATE products SET name = ?, price = ?, quantity = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getQuantity());
            stmt.setInt(4, product.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Success if rows were updated
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            return false; // Indicate failure
        }
    }


    // Method to delete a product
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

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

    private final ProductDao productDao;
    private final Connection connection;

    public ProductService(Connection connection) {
        this.connection = connection;
        this.productDao = new ProductDao(connection);
    }

    public boolean addProduct(String name, double price, int quantity, int categoryId) {
        if (name == null || name.trim().isEmpty() || price <= 0 || quantity <= 0 || categoryId <= 0) {
            System.out.println("Invalid input data.");
            return false;
        }

        if (doesProductExist(name)) {
            System.out.println("Product already exists: " + name);
            return false;
        }

        Product product = new Product(name, price, quantity, categoryId);
        return productDao.addProduct(product);
    }

    public boolean doesProductExist(String name) {
        return name != null && !name.trim().isEmpty() && productDao.doesProductExist(name);
    }

    public List<Product> getAllProducts() {
        String query = "SELECT * FROM products";
        List<Product> products = new ArrayList<>();

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

    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, price = ?, quantity = ?, category_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getQuantity());
            stmt.setInt(4, product.getCategoryId());
            stmt.setInt(5, product.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCategoryNameById(int categoryId) {
        String query = "SELECT name FROM categories WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Category"; // Default if category is not found
    }

    public List<Product> refreshProductList() {
        // Fetch the latest data from the database
        return productDao.getAllProducts(); // or similar method to fetch products from the database
    }

}

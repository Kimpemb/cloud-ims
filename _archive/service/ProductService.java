package com.joshuawilliams.ims.service;

import com.joshuawilliams.ims.dao.ProductDao;
import com.joshuawilliams.ims.model.Product;
import com.joshuawilliams.ims.model.ProductFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductService {

    private final ProductDao productDao;
    private final Connection connection;
    private ActivityLogService activityLogService;  // No final, allows reassignment

    public ProductService(ProductDao productDao, Connection connection) {
        this.productDao = productDao;
        this.connection = connection;
    }

    // Setter for activityLogService
    public void setActivityLogService(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }





    public boolean addProduct(String name, double price, int quantity, int categoryId) {
        if (name == null || name.trim().isEmpty() || price <= 0 || quantity <= 0 || categoryId <= 0) {
            System.out.println("Invalid product data.");
            return false;
        }

        if (doesProductExist(name)) {
            System.out.println("Product already exists: " + name);
            return false;
        }

        String productType = deriveProductType(categoryId);
        Product product = ProductFactory.create(0, name, price, quantity, categoryId, productType);

        try {
            if (productDao.addProduct(product)) {
                if (activityLogService != null) {
                    activityLogService.logActivity("Added product: " + name);
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error adding product: " + e.getMessage());
        }

        return false;
    }



    public boolean doesProductExist(String name) {
        return name != null && !name.trim().isEmpty() && productDao.doesProductExist(name);
    }

    /**
     * Maps a category to the product_type used to pick the right Product
     * subclass. Keeps the existing AddProductDialog UI (which only ever
     * collected a category, not a type) working unchanged — the type is
     * inferred rather than requiring a new UI field.
     */
    private String deriveProductType(int categoryId) {
        String categoryName = getCategoryNameById(categoryId);
        if (categoryName == null) {
            return "GENERAL";
        }
        String normalized = categoryName.trim().toUpperCase();
        return switch (normalized) {
            case "ELECTRONICS" -> "ELECTRONICS";
            case "CLOTHING" -> "CLOTHING";
            case "FOOD", "BEVERAGES", "TOFFEES" -> "GROCERY";
            default -> "GENERAL";
        };
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
                String productType = resultSet.getString("product_type");
                products.add(ProductFactory.create(id, name, price, quantity, categoryId, productType));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, price = ?, quantity = ?, category_id = ?, product_type = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getQuantity());
            stmt.setInt(4, product.getCategoryId());
            stmt.setString(5, product.getProductType());
            stmt.setInt(6, product.getId());
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

    public Product getProductById(int productId) {
        return productDao.getProductById(productId);
    }

    // ProductService.java
    public int getTotalProducts() {
        return productDao.getTotalProducts();
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
package com.joshuawilliams.ims.dao;

import com.joshuawilliams.ims.model.Category;
import com.joshuawilliams.ims.database.DatabaseConnection;

import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class CategoryDao {
    private Connection connection;

    public CategoryDao(Connection connection) {
        this.connection = connection;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT id, name FROM categories";  // Include the `id` column for full Category objects

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Create a new Category object for each row in the result set
                Category category = new Category();
                category.setId(rs.getInt("id"));       // Set the ID
                category.setName(rs.getString("name")); // Set the name
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public void addCategory(Category category) {
        // Check if the category already exists
        if (doesCategoryExist(category.getName())) {
            throw new IllegalArgumentException("Category with this name already exists.");
        }

        String query = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean doesCategoryExist(String categoryName) {
        String query = "SELECT COUNT(*) FROM categories WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, categoryName);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;  // Return true if the category exists, false otherwise
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Category> searchCategories(String searchQuery) {
        String query = "SELECT * FROM categories WHERE LOWER(name) LIKE ?";
        List<Category> categories = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + searchQuery.toLowerCase() + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Category category = new Category(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    );
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while searching for categories: " + e.getMessage());
        }
        return categories;
    }


    // Method to update a category
    public void updateCategory(Category category) {
        String query = "UPDATE categories SET name = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category.getName());
            statement.setInt(2, category.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();  // You should handle exceptions more gracefully
        }
    }

    // In CategoryDao.java
    public void deleteCategory(Category category) {
        String deleteQuery = "DELETE FROM categories WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setInt(1, category.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}

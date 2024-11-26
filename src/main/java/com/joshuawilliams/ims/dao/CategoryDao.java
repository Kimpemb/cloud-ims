package com.joshuawilliams.ims.dao;

import com.joshuawilliams.ims.model.Category;
import java.sql.*;
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
        String query = "SELECT COUNT(*) FROM categories WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, categoryName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;  // Return true if category exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

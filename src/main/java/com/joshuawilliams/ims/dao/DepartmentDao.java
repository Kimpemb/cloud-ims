package com.joshuawilliams.ims.dao;

import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDao {

    // Method to add a department to the database
    public void addDepartment(String name, String code, String description, String managerName, String email, String location, String status) {
        String sql = "INSERT INTO departments (name, code, description, manager_name, email, location, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, code);
            stmt.setString(3, description);
            stmt.setString(4, managerName);
            stmt.setString(5, email);
            stmt.setString(6, location);
            stmt.setString(7, status);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve all departments from the database
    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments"; // Adjust the table name and columns as per your database schema

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Department department = new Department();
                department.setId(rs.getString("id"));
                department.setName(rs.getString("name"));
                department.setCode(rs.getString("code"));
                department.setDescription(rs.getString("description"));
                department.setManagerName(rs.getString("manager_name"));
                department.setEmail(rs.getString("email"));
                department.setLocation(rs.getString("location"));
                department.setStatus(rs.getString("status"));

                departments.add(department);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return departments;
    }

    // Method to retrieve all department names
    public List<String> getAllDepartmentNames() {
        List<String> departmentNames = new ArrayList<>();
        String query = "SELECT name FROM departments"; // Adjust as per your schema

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                departmentNames.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return departmentNames;
    }


    // Method to update a department in the database
    public boolean updateDepartment(Department department) {
        String sql = "UPDATE departments SET name = ?, code = ?, description = ?, manager_name = ?, email = ?, location = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, department.getName());
            stmt.setString(2, department.getCode());
            stmt.setString(3, department.getDescription());
            stmt.setString(4, department.getManagerName());
            stmt.setString(5, department.getEmail());
            stmt.setString(6, department.getLocation());
            stmt.setString(7, department.getStatus());
            stmt.setString(8, department.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if update was successful
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Return false if update failed
    }

    // Method to delete a department from the database
    public boolean deleteDepartment(String departmentId) {
        String sql = "DELETE FROM departments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, departmentId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if deletion was successful
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Return false if deletion failed
    }
}

package com.joshuawilliams.ims.service;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogService {

    private final Connection connection;

    public ActivityLogService(Connection connection) {
        this.connection = connection;
    }

    public List<String> getRecentActivities() {
        List<String> activities = new ArrayList<>();
        String query = "SELECT description FROM activity_log ORDER BY timestamp DESC LIMIT 10";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String activity = resultSet.getString("description");
                System.out.println("Retrieved Activity: " + activity); // Debugging
                activities.add(activity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activities;
    }

    public void logActivity(String description) {
        String sql = "INSERT INTO activity_log (description, timestamp) VALUES (?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


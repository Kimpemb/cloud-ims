package com.joshuawilliams.ims.service;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SalesService {

    private final Connection connection;

    public SalesService(Connection connection) {
        this.connection = connection;
    }

    public double getTodaySales() {
        String sql = """
    SELECT COALESCE(SUM(total_price), 0) 
    FROM orders 
    WHERE DATE(order_date) = DATE('now') 
    OR DATE(order_date / 1000, 'unixepoch') = DATE('now')
""";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}

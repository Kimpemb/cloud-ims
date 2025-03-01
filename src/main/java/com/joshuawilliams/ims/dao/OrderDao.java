package com.joshuawilliams.ims.dao;

import com.joshuawilliams.ims.model.Customer;
import com.joshuawilliams.ims.model.Order;
import com.joshuawilliams.ims.model.Product;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

    private final Connection connection;

    public OrderDao(Connection connection) {
        this.connection = connection;
    }

    // Add a new order to the database
    public boolean addOrder(Order order) {
        String orderSql = "INSERT INTO orders (customer_id, total_price, order_date, created_by, created_by_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        String orderItemSql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";

        try (PreparedStatement orderStmt = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement orderItemStmt = connection.prepareStatement(orderItemSql)) {

            // Insert order details
            orderStmt.setString(1, order.getCustomer().getCustomerId());
            orderStmt.setDouble(2, order.getTotalAmount());
            orderStmt.setTimestamp(3, Timestamp.valueOf(order.getOrderDate()));
            orderStmt.setString(4, order.getProcessedBy());
            orderStmt.setInt(5, order.getProcessedById());

            int affectedRows = orderStmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Order insertion failed: No rows affected.");
                return false;
            }

            // Get the generated order ID
            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            int orderId;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            } else {
                System.err.println("Order insertion failed: No ID obtained.");
                return false;
            }

            // Insert items associated with the order
            List<Product> products = order.getProducts();
            List<Integer> quantities = order.getQuantities();

            for (int i = 0; i < products.size(); i++) {
                orderItemStmt.setInt(1, orderId);
                orderItemStmt.setInt(2, products.get(i).getId());
                orderItemStmt.setInt(3, quantities.get(i));
                orderItemStmt.addBatch();
            }

            orderItemStmt.executeBatch();
            return true;

        } catch (SQLException e) {
            System.err.println("Error adding order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve all orders from the database
// Retrieve all orders from the database
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT o.id, o.customer_id, o.total_price, o.order_date, o.created_by, o.created_by_id,
                   c.first_name, c.last_name
            FROM orders o
            JOIN customers c ON o.customer_id = c.customer_id
            """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LocalDateTime orderDate;

                try {
                    // Attempt to parse order_date as a timestamp (milliseconds)
                    long timestamp = rs.getLong("order_date");
                    orderDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
                } catch (SQLException e) {
                    System.err.println("Failed to parse order_date as a timestamp: " + e.getMessage());
                    orderDate = null; // Fallback if parsing fails
                }

                Order order = new Order(
                        rs.getInt("id"),
                        new Customer(
                                rs.getString("customer_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                null, null, null, null, null, null, 0, null, null
                        ),
                        new ArrayList<>(), // Products will be loaded separately if needed
                        new ArrayList<>(), // Quantities will be loaded separately if needed
                        rs.getDouble("total_price"),
                        orderDate,
                        rs.getString("created_by"),
                        rs.getInt("created_by_id")
                );
                orders.add(order);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving orders: " + e.getMessage());
            e.printStackTrace();
        }

        return orders;
    }


    // OrderDao.java
    public int getTotalOrders() {
        String query = "SELECT COUNT(*) FROM orders";
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


    // Retrieve a specific order by ID
    public Order getOrderById(int orderId) {
        String sql = """
                SELECT o.id, o.customer_id, o.total_price, o.order_date, o.created_by, o.created_by_id,
                       c.first_name, c.last_name
                FROM orders o
                JOIN customers c ON o.customer_id = c.customer_id
                WHERE o.id = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Order(
                        rs.getInt("id"),
                        new Customer(
                                rs.getString("customer_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                null, null, null, null, null, null, 0, null, null
                        ),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        rs.getDouble("total_price"),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getString("created_by"),
                        rs.getInt("created_by_id")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving order by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Delete an order by ID
    public boolean deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

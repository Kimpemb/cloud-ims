package com.joshuawilliams.ims.dao;

import com.joshuawilliams.ims.model.Supplier;
import com.joshuawilliams.ims.model.SupplierProductRelation;
import com.joshuawilliams.ims.model.SupplierOrderHistory;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Data Access Object for Supplier entities
 * Handles all database operations for suppliers
 */
public class SupplierDao {
    private final Connection connection;

    public SupplierDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Add a new supplier to the database
     */
    public boolean addSupplier(Supplier supplier) {
        String query = "INSERT INTO suppliers (id, name, email, phone_number, address, website_url, " +
                "category, bank_account_details, payment_terms, reliability_rating, delivery_performance, " +
                "status, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, supplier.getSupplierId() != null ? supplier.getSupplierId() : Supplier.generateSupplierId(getTotalSuppliers()));
            stmt.setString(2, supplier.getSupplierName() != null ? supplier.getSupplierName() : "");
            stmt.setString(3, supplier.getEmailAddress() != null ? supplier.getEmailAddress() : "");
            stmt.setString(4, supplier.getPhoneNumber() != null ? supplier.getPhoneNumber() : "");
            stmt.setString(5, supplier.getAddress() != null ? supplier.getAddress() : "");
            stmt.setString(6, supplier.getWebsiteUrl() != null ? supplier.getWebsiteUrl() : "");
            stmt.setString(7, supplier.getCategory() != null ? supplier.getCategory() : "Uncategorized");
            stmt.setString(8, supplier.getBankAccountDetails() != null ? supplier.getBankAccountDetails() : "");
            stmt.setString(9, supplier.getPaymentTerms() != null ? supplier.getPaymentTerms() : "");
            stmt.setInt(10, supplier.getReliabilityRating());
            stmt.setInt(11, supplier.getDeliveryPerformance());
            stmt.setString(12, supplier.getStatus() != null ? supplier.getStatus() : "Active");
            stmt.setString(13, supplier.getNotes() != null ? supplier.getNotes() : "");

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add supplier: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieve all suppliers from the database
     */
    public List<Supplier> getAllSuppliers() {
        String sql = "SELECT * FROM suppliers ORDER BY name";
        List<Supplier> suppliers = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    /**
     * Get a supplier by ID
     */
    public Supplier getSupplierById(String supplierId) {
        String sql = "SELECT * FROM suppliers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSupplier(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update an existing supplier's information
     */
    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE suppliers SET name = ?, email = ?, phone_number = ?, address = ?, " +
                "website_url = ?, category = ?, bank_account_details = ?, payment_terms = ?, " +
                "reliability_rating = ?, delivery_performance = ?, status = ?, notes = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplier.getSupplierName());
            stmt.setString(2, supplier.getEmailAddress());
            stmt.setString(3, supplier.getPhoneNumber());
            stmt.setString(4, supplier.getAddress());
            stmt.setString(5, supplier.getWebsiteUrl());
            stmt.setString(6, supplier.getCategory());
            stmt.setString(7, supplier.getBankAccountDetails());
            stmt.setString(8, supplier.getPaymentTerms());
            stmt.setInt(9, supplier.getReliabilityRating());
            stmt.setInt(10, supplier.getDeliveryPerformance());
            stmt.setString(11, supplier.getStatus());
            stmt.setString(12, supplier.getNotes());
            stmt.setString(13, supplier.getSupplierId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a supplier by ID
     */
    public boolean deleteSupplier(String supplierId) {
        String sql = "DELETE FROM suppliers WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplierId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the total number of suppliers
     */
    public int getTotalSuppliers() {
        String query = "SELECT COUNT(*) FROM suppliers";
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

    /**
     * Search suppliers by various criteria
     */
    public List<Supplier> searchSuppliers(Map<String, Object> criteria) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM suppliers WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (criteria.containsKey("name")) {
            sqlBuilder.append(" AND name LIKE ?");
            params.add("%" + criteria.get("name") + "%");
        }

        if (criteria.containsKey("email")) {
            sqlBuilder.append(" AND email LIKE ?");
            params.add("%" + criteria.get("email") + "%");
        }

        if (criteria.containsKey("category")) {
            sqlBuilder.append(" AND category LIKE ?");
            params.add("%" + criteria.get("category") + "%");
        }

        if (criteria.containsKey("status")) {
            sqlBuilder.append(" AND status = ?");
            params.add(criteria.get("status"));
        }

        if (criteria.containsKey("minReliability")) {
            sqlBuilder.append(" AND reliability_rating >= ?");
            params.add(criteria.get("minReliability"));
        }

        if (criteria.containsKey("minDeliveryPerformance")) {
            sqlBuilder.append(" AND delivery_performance >= ?");
            params.add(criteria.get("minDeliveryPerformance"));
        }

        sqlBuilder.append(" ORDER BY name");

        List<Supplier> suppliers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(mapResultSetToSupplier(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suppliers;
    }

    /**
     * Get products supplied by a specific supplier
     */
    public List<SupplierProductRelation> getSupplierProducts(String supplierId) {
        String sql = "SELECT * FROM supplier_products WHERE supplier_id = ?";
        List<SupplierProductRelation> relations = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    relations.add(new SupplierProductRelation(
                            rs.getString("supplier_id"),
                            rs.getString("product_id"),
                            rs.getDouble("unit_price"),
                            rs.getInt("min_order_quantity"),
                            rs.getInt("lead_time_days")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return relations;
    }

    /**
     * Save a supplier-product relationship
     */
    public boolean saveSupplierProductRelation(SupplierProductRelation relation) {
        String sql = "INSERT INTO supplier_products (supplier_id, product_id, unit_price, min_order_quantity, lead_time_days) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE unit_price = ?, min_order_quantity = ?, lead_time_days = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, relation.getSupplierId());
            stmt.setString(2, relation.getProductId());
            stmt.setDouble(3, relation.getUnitPrice());
            stmt.setInt(4, relation.getMinOrderQuantity());
            stmt.setInt(5, relation.getLeadTimeDays());
            // For the ON DUPLICATE KEY part
            stmt.setDouble(6, relation.getUnitPrice());
            stmt.setInt(7, relation.getMinOrderQuantity());
            stmt.setInt(8, relation.getLeadTimeDays());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a supplier-product relationship
     */
    public boolean deleteSupplierProductRelation(String supplierId, String productId) {
        String sql = "DELETE FROM supplier_products WHERE supplier_id = ? AND product_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplierId);
            stmt.setString(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get supplier order history records
     */
    public List<SupplierOrderHistory> getSupplierOrderHistory(String supplierId) {
        String sql = "SELECT * FROM supplier_order_history WHERE supplier_id = ? ORDER BY order_date DESC";
        List<SupplierOrderHistory> history = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SupplierOrderHistory record = new SupplierOrderHistory(
                            rs.getString("order_id"),
                            rs.getString("supplier_id"),
                            rs.getDate("order_date"),
                            rs.getDouble("total_amount"),
                            rs.getString("status"),
                            rs.getInt("delivery_days"),
                            rs.getBoolean("on_time_delivery")
                    );
                    history.add(record);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    /**
     * Save a supplier order history record
     */
    public boolean saveSupplierOrderHistory(SupplierOrderHistory history) {
        String sql = "INSERT INTO supplier_order_history (order_id, supplier_id, order_date, total_amount, " +
                "status, delivery_days, on_time_delivery) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, history.getOrderId());
            stmt.setString(2, history.getSupplierId());
            stmt.setDate(3, new java.sql.Date(history.getOrderDate().getTime()));
            stmt.setDouble(4, history.getTotalAmount());
            stmt.setString(5, history.getStatus());
            stmt.setInt(6, history.getDeliveryDays());
            stmt.setBoolean(7, history.isOnTimeDelivery());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to map ResultSet to Supplier object
     */
    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone_number"),
                rs.getString("address"),
                rs.getString("website_url"),
                rs.getString("category"),
                rs.getString("bank_account_details"),
                rs.getString("payment_terms"),
                rs.getInt("reliability_rating"),
                rs.getInt("delivery_performance"),
                rs.getString("status"),
                rs.getString("notes")
        );

        // Try to get additional fields if they exist in the result set
        try {
            Date lastOrderDate = rs.getDate("last_order_date");
            if (lastOrderDate != null && !rs.wasNull()) {
                supplier.setLastOrderDate(lastOrderDate);
            }

            int totalOrders = rs.getInt("total_orders");
            if (!rs.wasNull()) {
                supplier.setTotalOrdersPlaced(totalOrders);
            }

            double avgResponseTime = rs.getDouble("avg_response_time");
            if (!rs.wasNull()) {
                supplier.setAverageResponseTime(avgResponseTime);
            }
        } catch (SQLException e) {
            // These fields might not exist in the current schema, which is okay
        }

        return supplier;
    }
}
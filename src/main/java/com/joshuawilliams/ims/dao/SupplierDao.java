package com.joshuawilliams.ims.dao;

import com.joshuawilliams.ims.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDao {

    private final Connection connection;

    public SupplierDao(Connection connection) {
        this.connection = connection;
    }

    // Add a new supplier
    public boolean addSupplier(Supplier supplier) {
        String query = "INSERT INTO suppliers (name, email, phone_number, address, website_url, category, bank_account_details, payment_terms, status, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, supplier.getSupplierName());
            stmt.setString(2, supplier.getEmailAddress());
            stmt.setString(3, supplier.getPhoneNumber());
            stmt.setString(4, supplier.getAddress());
            stmt.setString(5, supplier.getWebsiteUrl());
            stmt.setString(6, supplier.getCategory());
            stmt.setString(7, supplier.getBankAccountDetails());
            stmt.setString(8, supplier.getPaymentTerms());
            stmt.setString(9, supplier.getStatus());
            stmt.setString(10, supplier.getNotes());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve all suppliers
    public List<Supplier> getAllSuppliers() {
        String sql = "SELECT * FROM suppliers";
        List<Supplier> suppliers = new ArrayList<>();

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    // Get supplier by ID
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

    // Update supplier details
    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE suppliers SET name = ?, email = ?, phone_number = ?, address = ?, website_url = ?, category = ?, bank_account_details = ?, payment_terms = ?, status = ?, notes = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplier.getSupplierName());
            stmt.setString(2, supplier.getEmailAddress());
            stmt.setString(3, supplier.getPhoneNumber());
            stmt.setString(4, supplier.getAddress());
            stmt.setString(5, supplier.getWebsiteUrl());
            stmt.setString(6, supplier.getCategory());
            stmt.setString(7, supplier.getBankAccountDetails());
            stmt.setString(8, supplier.getPaymentTerms());
            stmt.setString(9, supplier.getStatus());
            stmt.setString(10, supplier.getNotes());
            stmt.setString(11, supplier.getSupplierId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete supplier by ID
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

    // Get suppliers by reliability rating threshold
    public List<Supplier> getSuppliersByReliabilityRating(int ratingThreshold) {
        String sql = "SELECT * FROM suppliers WHERE reliability_rating >= ?";
        List<Supplier> suppliers = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ratingThreshold);
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

    // Get suppliers by delivery performance threshold
    public List<Supplier> getSuppliersByDeliveryPerformance(int performanceThreshold) {
        String sql = "SELECT * FROM suppliers WHERE delivery_performance >= ?";
        List<Supplier> suppliers = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, performanceThreshold);
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

    // Get suppliers by name
    public List<Supplier> getSuppliersByName(String name) {
        String sql = "SELECT * FROM suppliers WHERE name LIKE ?";
        List<Supplier> suppliers = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
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

    // Get suppliers by email
    public List<Supplier> getSuppliersByEmail(String email) {
        String sql = "SELECT * FROM suppliers WHERE email LIKE ?";
        List<Supplier> suppliers = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + email + "%");
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

    // Get suppliers by category
    public List<Supplier> getSuppliersByCategory(String category) {
        String sql = "SELECT * FROM suppliers WHERE category LIKE ?";
        List<Supplier> suppliers = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + category + "%");
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

    // SupplierDao.java
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


    // Helper method to map ResultSet to Supplier object
    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        return new Supplier(
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
    }
}

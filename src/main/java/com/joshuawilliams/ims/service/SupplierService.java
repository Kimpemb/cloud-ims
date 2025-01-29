package com.joshuawilliams.ims.service;

import com.joshuawilliams.ims.dao.SupplierDao;
import com.joshuawilliams.ims.database.DatabaseConnection;
import com.joshuawilliams.ims.model.Supplier;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SupplierService {
    private final SupplierDao supplierDao;
    private final Connection connection;

    // Constructor
    public SupplierService() {
        this.connection = DatabaseConnection.getConnection();
        this.supplierDao = new SupplierDao(connection);
    }

    public boolean addSupplier(Supplier supplier) {
        validateSupplier(supplier);
        if (supplier.getSupplierId() == null || supplier.getSupplierId().isEmpty()) {
            int supplierCount = getSupplierCount();
            supplier.setSupplierId(generateSupplierId(supplierCount));
        }
        return supplierDao.addSupplier(supplier);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierDao.getAllSuppliers();
    }

    public Supplier getSupplierById(String supplierId) {
        validateString(supplierId, "Supplier ID");
        Supplier supplier = supplierDao.getSupplierById(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + supplierId + " not found.");
        }
        return supplier;
    }

    public boolean updateSupplier(Supplier supplier) {
        validateSupplier(supplier);
        return supplierDao.updateSupplier(supplier);
    }

    public boolean deleteSupplier(String supplierId) {
        validateString(supplierId, "Supplier ID");
        return supplierDao.deleteSupplier(supplierId);
    }

    public void close() {
        DatabaseConnection.closeConnection(connection);
    }

    public List<Supplier> searchSuppliersByName(String name) {
        validateString(name, "Supplier name");
        return supplierDao.getSuppliersByName(name);
    }

    public List<Supplier> searchSuppliersByEmail(String email) {
        validateString(email, "Email");
        return supplierDao.getSuppliersByEmail(email);
    }

    public List<Supplier> searchSuppliersByCategory(String category) {
        validateString(category, "Category");
        return supplierDao.getSuppliersByCategory(category);
    }

    public List<Supplier> searchSuppliersByReliabilityRating(int rating) {
        validateRating(rating, "Reliability rating");
        return supplierDao.getSuppliersByReliabilityRating(rating);
    }

    public List<Supplier> searchSuppliersByDeliveryPerformance(int performance) {
        validateRating(performance, "Delivery performance");
        return supplierDao.getSuppliersByDeliveryPerformance(performance);
    }

    public int getSupplierCount() {
        return supplierDao.getAllSuppliers().size();
    }

    public String generateSupplierId(int supplierCount) {
        String year = new SimpleDateFormat("yyyy").format(new Date());
        String sequentialNumber = String.format("%03d", supplierCount + 1);
        return "SUP-" + year + "-" + sequentialNumber;
    }

    private void validateSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null.");
        }
        validateString(supplier.getSupplierName(), "Supplier name");
        if (supplier.getEmailAddress() != null && !supplier.getEmailAddress().contains("@")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }

    private void validateString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
        }
    }

    private void validateRating(int value, String fieldName) {
        if (value < 0 || value > 10) {
            throw new IllegalArgumentException(fieldName + " must be between 0 and 10.");
        }
    }
}

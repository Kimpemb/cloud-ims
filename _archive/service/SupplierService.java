package com.joshuawilliams.ims.service;

import com.joshuawilliams.ims.dao.SupplierDao;
import com.joshuawilliams.ims.model.Supplier;
import com.joshuawilliams.ims.model.SupplierProductRelation;
import com.joshuawilliams.ims.utils.SupplierExporter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Service class for managing suppliers, including CRUD operations,
 * performance tracking, product relations, and data export.
 */
public class SupplierService {
    private final SupplierDao supplierDAO;

    private static final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PHONE_PATTERN = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$";

    public SupplierService(SupplierDao supplierDao, Connection connection) {
        this.supplierDAO = new SupplierDao(connection);
    }

    // ==================== CRUD ====================

    public String addSupplier(Supplier supplier) throws SQLException {
        validateSupplier(supplier);

        if (isBlank(supplier.getSupplierId())) {
            supplier.setSupplierId(Supplier.generateSupplierId(getSupplierCount()));
        }


        return supplierDAO.addSupplier(supplier) ? supplier.getSupplierId() : null;
    }

    public boolean updateSupplier(Supplier supplier) throws SQLException {
        validateString(supplier.getSupplierId(), "Supplier ID");
        validateSupplier(supplier);
        return supplierDAO.updateSupplier(supplier);
    }

    public boolean deleteSupplier(String supplierId) throws SQLException {
        validateString(supplierId, "Supplier ID");
        return supplierDAO.deleteSupplier(supplierId);
    }

    public boolean toggleSupplierStatus(String supplierId) throws SQLException {
        Supplier supplier = getSupplierById(supplierId);
        supplier.setStatus(supplier.getStatus().equalsIgnoreCase("Active") ? "Inactive" : "Active");
        return updateSupplier(supplier);
    }

    // ==================== GETTERS ====================

    public ObservableList<Supplier> getAllSuppliers() throws SQLException {
        return FXCollections.observableArrayList(supplierDAO.getAllSuppliers());
    }

    public Supplier getSupplierById(String supplierId) throws SQLException {
        validateString(supplierId, "Supplier ID");
        Supplier supplier = supplierDAO.getSupplierById(supplierId);
        if (supplier == null) throw new IllegalArgumentException("Supplier not found: " + supplierId);
        return supplier;
    }

    public int getSupplierCount() throws SQLException {
        return supplierDAO.getTotalSuppliers();
    }

    public ObservableList<String> getAllCategories() throws SQLException {
        Set<String> categories = new TreeSet<>();
        for (Supplier s : getAllSuppliers()) {
            if (!isBlank(s.getCategory())) categories.add(s.getCategory());
        }
        return FXCollections.observableArrayList(categories);
    }

    // ==================== SEARCH ====================

    public ObservableList<Supplier> searchSuppliers(Map<String, Object> criteria) throws SQLException {
        return FXCollections.observableArrayList(supplierDAO.searchSuppliers(criteria));
    }

    // ==================== RELATIONS ====================

    public boolean saveSupplierProductRelation(SupplierProductRelation relation) throws SQLException {
        validateSupplierProductRelation(relation);
        return supplierDAO.saveSupplierProductRelation(relation);
    }

    public ObservableList<SupplierProductRelation> getSupplierProducts(String supplierId) throws SQLException {
        validateString(supplierId, "Supplier ID");
        return FXCollections.observableArrayList(supplierDAO.getSupplierProducts(supplierId));
    }

    public boolean deleteSupplierProductRelation(String supplierId, String productId) throws SQLException {
        validateString(supplierId, "Supplier ID");
        validateString(productId, "Product ID");
        return supplierDAO.deleteSupplierProductRelation(supplierId, productId);
    }

    // ==================== METRICS ====================

    public Map<String, Object> calculateSupplierMetrics(String supplierId) throws SQLException {
        Supplier supplier = getSupplierById(supplierId);
        Map<String, Object> metrics = new LinkedHashMap<>();

        metrics.put("Supplier ID", supplier.getSupplierId());
        metrics.put("Supplier Name", supplier.getSupplierName());
        metrics.put("Status", supplier.getStatus());
        metrics.put("Reliability Rating", supplier.getReliabilityRating() + "/5");
        metrics.put("Delivery Performance", supplier.getDeliveryPerformance() + "/5");
        metrics.put("Overall Score", supplier.calculatePerformanceScore() + "/100");
        metrics.put("Total Orders", supplier.getTotalOrdersPlaced());
        metrics.put("Avg Response Time", String.format("%.1f days", supplier.getAverageResponseTime()));
        metrics.put("Last Order Date", supplier.getLastOrderDate() != null ?
                supplier.getLastOrderDate().toString() : "Never");

        return metrics;
    }

    public boolean updateSupplierPerformance(String supplierId, int reliabilityRating, int deliveryPerformance)
            throws SQLException {
        validateRating(reliabilityRating);
        validateRating(deliveryPerformance);

        Supplier supplier = getSupplierById(supplierId);
        supplier.setReliabilityRating(reliabilityRating);
        supplier.setDeliveryPerformance(deliveryPerformance);

        return updateSupplier(supplier);
    }

    public void recordSupplierOrder(String supplierId, Date orderDate, double responseTime) throws SQLException {
        Supplier supplier = getSupplierById(supplierId);
        supplier.updateMetricsAfterOrder(orderDate, responseTime);
        updateSupplier(supplier);
    }

    // ==================== EXPORT ====================

    public void exportAllSuppliers(File exportFile, String exportType) throws IOException, SQLException {
        exportSuppliers(getAllSuppliers(), exportFile, exportType);
    }

    public void exportSupplierData(String supplierId, File exportFile, String exportType)
            throws IOException, SQLException {
        List<Supplier> single = Collections.singletonList(getSupplierById(supplierId));
        exportSuppliers(single, exportFile, exportType);
    }

    private void exportSuppliers(List<Supplier> suppliers, File exportFile, String exportType) throws IOException {
        String path = exportFile.getAbsolutePath();

        switch (exportType.toLowerCase()) {
            case "excel" -> SupplierExporter.exportToExcel(suppliers, path);
            case "csv" -> SupplierExporter.exportToCSV(suppliers, path);
            case "pdf" -> SupplierExporter.exportToPDF(suppliers, path);
            default -> throw new IllegalArgumentException("Unsupported export type: " + exportType);
        }
    }

    // ==================== VALIDATION ====================

    private void validateSupplier(Supplier supplier) {
        if (supplier == null) throw new IllegalArgumentException("Supplier cannot be null.");
        validateString(supplier.getSupplierName(), "Supplier Name");
        validateEmail(supplier.getEmailAddress());
        validatePhone(supplier.getPhoneNumber());
    }

    private void validateSupplierProductRelation(SupplierProductRelation relation) {
        if (relation == null) throw new IllegalArgumentException("Relation cannot be null.");
        validateString(relation.getSupplierId(), "Supplier ID");
        validateString(relation.getProductId(), "Product ID");
        if (relation.getUnitPrice() <= 0) throw new IllegalArgumentException("Unit price must be positive.");
        if (relation.getMinOrderQuantity() <= 0) throw new IllegalArgumentException("Minimum order must be positive.");
        if (relation.getLeadTimeDays() < 0) throw new IllegalArgumentException("Lead time cannot be negative.");
    }

    private void validateString(String value, String field) {
        if (isBlank(value)) throw new IllegalArgumentException(field + " cannot be empty.");
    }

    private void validateEmail(String email) {
        if (!isBlank(email) && !email.matches(EMAIL_PATTERN)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }

    private void validatePhone(String phone) {
        if (!isBlank(phone) && !phone.matches(PHONE_PATTERN)) {
            throw new IllegalArgumentException("Invalid phone number format.");
        }
    }

    private void validateRating(int rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5.");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}

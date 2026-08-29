package com.joshuawilliams.ims.model;

import javafx.beans.property.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Entity class representing a supplier, including contact info,
 * performance metrics, financial details, and historical data.
 */
public class Supplier {

    // Basic info
    private final StringProperty supplierId;
    private final StringProperty supplierName;
    private final StringProperty emailAddress;
    private final StringProperty phoneNumber;
    private final StringProperty address;
    private final StringProperty websiteUrl;
    private final StringProperty category;

    // Financial info
    private final StringProperty bankAccountDetails;
    private final StringProperty paymentTerms;

    // Performance metrics
    private final IntegerProperty reliabilityRating;
    private final IntegerProperty deliveryPerformance;
    private final StringProperty status;
    private final StringProperty notes;

    // Tracking
    private final ObjectProperty<Date> lastOrderDate;
    private final IntegerProperty totalOrdersPlaced;
    private final DoubleProperty averageResponseTime;

    /**
     * Full constructor
     */
    public Supplier(String supplierId, String supplierName, String emailAddress, String phoneNumber,
                    String address, String websiteUrl, String category, String bankAccountDetails,
                    String paymentTerms, int reliabilityRating, int deliveryPerformance,
                    String status, String notes) {

        this.supplierId = new SimpleStringProperty(supplierId);
        this.supplierName = new SimpleStringProperty(supplierName);
        this.emailAddress = new SimpleStringProperty(emailAddress);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.address = new SimpleStringProperty(address);
        this.websiteUrl = new SimpleStringProperty(websiteUrl);
        this.category = new SimpleStringProperty(category);
        this.bankAccountDetails = new SimpleStringProperty(bankAccountDetails);
        this.paymentTerms = new SimpleStringProperty(paymentTerms);
        this.reliabilityRating = new SimpleIntegerProperty(reliabilityRating);
        this.deliveryPerformance = new SimpleIntegerProperty(deliveryPerformance);
        this.status = new SimpleStringProperty(status);
        this.notes = new SimpleStringProperty(notes);

        this.lastOrderDate = new SimpleObjectProperty<>(null);
        this.totalOrdersPlaced = new SimpleIntegerProperty(0);
        this.averageResponseTime = new SimpleDoubleProperty(0.0);
    }

    /**
     * Minimal constructor with essential fields
     */
    public Supplier(String supplierName, String emailAddress, String phoneNumber, String category) {
        this(generateSupplierId(0), supplierName, emailAddress, phoneNumber,
                "", "", category, "", "", 0, 0, "Active", "");
    }

    /**
     * Constructor for dialog: name, email, phone, category, status, reliability
     */
    public Supplier(String supplierName, String emailAddress, String phoneNumber,
                    String category, String status, int reliabilityRating) {
        this(generateSupplierId(0), supplierName, emailAddress, phoneNumber,
                "", "", category, "", "", reliabilityRating, 0, status, "");
    }

    /**
     * Constructor that matches: Supplier(String, String, String, String, String, String, int)
     * Used in certain simplified contexts or UI dialogs
     */
    public Supplier(String supplierName, String emailAddress, String phoneNumber,
                    String category, String status, String supplierId, int reliabilityRating) {
        this(supplierId, supplierName, emailAddress, phoneNumber,
                "", "", category, "", "", reliabilityRating, 0, status, "");
    }

    // ─────────── Property Getters ───────────
    public StringProperty supplierIdProperty() { return supplierId; }
    public StringProperty supplierNameProperty() { return supplierName; }
    public StringProperty emailAddressProperty() { return emailAddress; }
    public StringProperty phoneNumberProperty() { return phoneNumber; }
    public StringProperty addressProperty() { return address; }
    public StringProperty websiteUrlProperty() { return websiteUrl; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty bankAccountDetailsProperty() { return bankAccountDetails; }
    public StringProperty paymentTermsProperty() { return paymentTerms; }
    public IntegerProperty reliabilityRatingProperty() { return reliabilityRating; }
    public IntegerProperty deliveryPerformanceProperty() { return deliveryPerformance; }
    public StringProperty statusProperty() { return status; }
    public StringProperty notesProperty() { return notes; }
    public ObjectProperty<Date> lastOrderDateProperty() { return lastOrderDate; }
    public IntegerProperty totalOrdersPlacedProperty() { return totalOrdersPlaced; }
    public DoubleProperty averageResponseTimeProperty() { return averageResponseTime; }

    // ─────────── Field Getters ───────────
    public String getSupplierId() { return supplierId.get(); }
    public String getSupplierName() { return supplierName.get(); }
    public String getEmailAddress() { return emailAddress.get(); }
    public String getPhoneNumber() { return phoneNumber.get(); }
    public String getAddress() { return address.get(); }
    public String getWebsiteUrl() { return websiteUrl.get(); }
    public String getCategory() { return category.get(); }
    public String getBankAccountDetails() { return bankAccountDetails.get(); }
    public String getPaymentTerms() { return paymentTerms.get(); }
    public int getReliabilityRating() { return reliabilityRating.get(); }
    public int getDeliveryPerformance() { return deliveryPerformance.get(); }
    public String getStatus() { return status.get(); }
    public String getNotes() { return notes.get(); }
    public Date getLastOrderDate() { return lastOrderDate.get(); }
    public int getTotalOrdersPlaced() { return totalOrdersPlaced.get(); }
    public double getAverageResponseTime() { return averageResponseTime.get(); }

    // ─────────── Field Setters ───────────
    public void setSupplierId(String supplierId) { this.supplierId.set(supplierId); }
    public void setSupplierName(String supplierName) { this.supplierName.set(supplierName); }
    public void setEmailAddress(String emailAddress) { this.emailAddress.set(emailAddress); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public void setAddress(String address) { this.address.set(address); }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl.set(websiteUrl); }
    public void setCategory(String category) { this.category.set(category); }
    public void setBankAccountDetails(String bankAccountDetails) { this.bankAccountDetails.set(bankAccountDetails); }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms.set(paymentTerms); }
    public void setReliabilityRating(int reliabilityRating) { this.reliabilityRating.set(reliabilityRating); }
    public void setDeliveryPerformance(int deliveryPerformance) { this.deliveryPerformance.set(deliveryPerformance); }
    public void setStatus(String status) { this.status.set(status); }
    public void setNotes(String notes) { this.notes.set(notes); }
    public void setLastOrderDate(Date date) { this.lastOrderDate.set(date); }
    public void setTotalOrdersPlaced(int count) { this.totalOrdersPlaced.set(count); }
    public void setAverageResponseTime(double time) { this.averageResponseTime.set(time); }

    // ─────────── Utility Methods ───────────

    /**
     * Generate unique supplier ID based on count and current year.
     */
    public static String generateSupplierId(int supplierCount) {
        String year = new SimpleDateFormat("yyyy").format(new Date());
        String sequential = String.format("%03d", supplierCount + 1);
        return "SUP-" + year + "-" + sequential;
    }

    /**
     * Update performance tracking after a new order.
     */
    public void updateMetricsAfterOrder(Date orderDate, double responseTime) {
        lastOrderDate.set(orderDate);
        int currentOrders = getTotalOrdersPlaced() + 1;
        double newAvg = ((getAverageResponseTime() * getTotalOrdersPlaced()) + responseTime) / currentOrders;

        setTotalOrdersPlaced(currentOrders);
        setAverageResponseTime(newAvg);
    }

    /**
     * Calculate supplier performance score (0–100).
     */
    public int calculatePerformanceScore() {
        return (getReliabilityRating() + getDeliveryPerformance()) * 5;
    }

    @Override
    public String toString() {
        return getSupplierName() + " (" + getSupplierId() + ")";
    }
}

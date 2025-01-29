package com.joshuawilliams.ims.model;

import javafx.beans.property.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Supplier {
    private final StringProperty supplierId;  // Auto-generated field
    private final StringProperty supplierName;
    private final StringProperty emailAddress;
    private final StringProperty phoneNumber;
    private final StringProperty address;
    private final StringProperty websiteUrl;
    private final StringProperty category;
    private final StringProperty bankAccountDetails;
    private final StringProperty paymentTerms;
    private final IntegerProperty reliabilityRating;
    private final IntegerProperty deliveryPerformance;
    private final StringProperty status;
    private final StringProperty notes;

    // Constructor for creating a supplier with all fields
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
    }


    // Generate Supplier ID (e.g., SUP-YYYY-001)
    public String generateSupplierId(int supplierCount) {
        String year = new SimpleDateFormat("yyyy").format(new Date());
        String sequentialNumber = String.format("%03d", supplierCount); // Ensure 3-digit format
        return "SUP-" + year + "-" + sequentialNumber;
    }

    // Property Getters
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

    // Field Getters
    public String getSupplierId() { return supplierId.get(); }
    public String getSupplierName() { return supplierName.get(); }
    public String getEmailAddress() { return emailAddress.get(); }
    public String getPhoneNumber() { return phoneNumber.get(); }
    public String getAddress() { return address.get(); }
    public String getWebsiteUrl() { return websiteUrl.get(); }
    public String getCategory() { return category.get(); }
    public String getBankAccountDetails() { return bankAccountDetails.get(); }
    public String getPaymentTerms() { return paymentTerms.get(); }
    public Integer getReliabilityRating() { return reliabilityRating.get(); }
    public Integer getDeliveryPerformance() { return deliveryPerformance.get(); }
    public String getStatus() { return status.get(); }
    public String getNotes() { return notes.get(); }

    // Field Setters
    public void setSupplierId(String supplierId) { this.supplierId.set(supplierId); }
    public void setSupplierName(String supplierName) { this.supplierName.set(supplierName); }
    public void setEmailAddress(String emailAddress) { this.emailAddress.set(emailAddress); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public void setAddress(String address) { this.address.set(address); }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl.set(websiteUrl); }
    public void setCategory(String category) { this.category.set(category); }
    public void setBankAccountDetails(String bankAccountDetails) { this.bankAccountDetails.set(bankAccountDetails); }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms.set(paymentTerms); }
    public void setReliabilityRating(Integer reliabilityRating) { this.reliabilityRating.set(reliabilityRating); }
    public void setDeliveryPerformance(Integer deliveryPerformance) { this.deliveryPerformance.set(deliveryPerformance); }
    public void setStatus(String status) { this.status.set(status); }
    public void setNotes(String notes) { this.notes.set(notes); }

    // Additional methods for updates
    public void updateReliabilityRating(Integer rating) { this.reliabilityRating.set(rating); }
    public void updateDeliveryPerformance(Integer performance) { this.deliveryPerformance.set(performance); }
}

package com.joshuawilliams.ims.model;

import javafx.beans.property.*;

/**
 * Represents the relationship between suppliers and products
 * including pricing, order quantities, and lead times
 */
public class SupplierProductRelation {
    private final StringProperty supplierId;
    private final StringProperty productId;
    private final DoubleProperty unitPrice;
    private final IntegerProperty minOrderQuantity;
    private final IntegerProperty leadTimeDays;

    public SupplierProductRelation(String supplierId, String productId, double unitPrice,
                                   int minOrderQuantity, int leadTimeDays) {
        this.supplierId = new SimpleStringProperty(supplierId);
        this.productId = new SimpleStringProperty(productId);
        this.unitPrice = new SimpleDoubleProperty(unitPrice);
        this.minOrderQuantity = new SimpleIntegerProperty(minOrderQuantity);
        this.leadTimeDays = new SimpleIntegerProperty(leadTimeDays);
    }

    // Property getters
    public StringProperty supplierIdProperty() { return supplierId; }
    public StringProperty productIdProperty() { return productId; }
    public DoubleProperty unitPriceProperty() { return unitPrice; }
    public IntegerProperty minOrderQuantityProperty() { return minOrderQuantity; }
    public IntegerProperty leadTimeDaysProperty() { return leadTimeDays; }

    // Field getters
    public String getSupplierId() { return supplierId.get(); }
    public String getProductId() { return productId.get(); }
    public double getUnitPrice() { return unitPrice.get(); }
    public int getMinOrderQuantity() { return minOrderQuantity.get(); }
    public int getLeadTimeDays() { return leadTimeDays.get(); }

    // Field setters
    public void setSupplierId(String supplierId) { this.supplierId.set(supplierId); }
    public void setProductId(String productId) { this.productId.set(productId); }
    public void setUnitPrice(double unitPrice) { this.unitPrice.set(unitPrice); }
    public void setMinOrderQuantity(int minOrderQuantity) { this.minOrderQuantity.set(minOrderQuantity); }
    public void setLeadTimeDays(int leadTimeDays) { this.leadTimeDays.set(leadTimeDays); }

    @Override
    public String toString() {
        return "Product: " + productId.get() + " - Price: " + unitPrice.get();
    }
}
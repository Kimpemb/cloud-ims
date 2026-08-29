package com.joshuawilliams.ims.model;

import javafx.beans.property.*;
import java.util.Date;

/**
 * Entity class representing a supplier order history record
 * Contains information about orders placed with suppliers, delivery times, and status
 */
public class SupplierOrderHistory {
    private final StringProperty orderId;
    private final StringProperty supplierId;
    private final ObjectProperty<Date> orderDate;
    private final DoubleProperty totalAmount;
    private final StringProperty status;
    private final IntegerProperty deliveryDays;
    private final BooleanProperty onTimeDelivery;
    private final StringProperty notes;
    private final ObjectProperty<Date> deliveryDate;

    /**
     * Complete constructor for SupplierOrderHistory
     */
    public SupplierOrderHistory(String orderId, String supplierId, Date orderDate,
                                double totalAmount, String status, int deliveryDays,
                                boolean onTimeDelivery) {
        this.orderId = new SimpleStringProperty(orderId);
        this.supplierId = new SimpleStringProperty(supplierId);
        this.orderDate = new SimpleObjectProperty<>(orderDate);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.status = new SimpleStringProperty(status);
        this.deliveryDays = new SimpleIntegerProperty(deliveryDays);
        this.onTimeDelivery = new SimpleBooleanProperty(onTimeDelivery);
        this.notes = new SimpleStringProperty("");

        // Calculate delivery date based on order date and delivery days
        Date calculatedDeliveryDate = null;
        if (orderDate != null) {
            calculatedDeliveryDate = new Date(orderDate.getTime() + (deliveryDays * 86400000L));
        }
        this.deliveryDate = new SimpleObjectProperty<>(calculatedDeliveryDate);
    }

    /**
     * Minimal constructor for creating a new order history record
     */
    public SupplierOrderHistory(String orderId, String supplierId, Date orderDate, double totalAmount) {
        this(orderId, supplierId, orderDate, totalAmount, "Pending", 0, false);
    }

    // Property Getters
    public StringProperty orderIdProperty() { return orderId; }
    public StringProperty supplierIdProperty() { return supplierId; }
    public ObjectProperty<Date> orderDateProperty() { return orderDate; }
    public DoubleProperty totalAmountProperty() { return totalAmount; }
    public StringProperty statusProperty() { return status; }
    public IntegerProperty deliveryDaysProperty() { return deliveryDays; }
    public BooleanProperty onTimeDeliveryProperty() { return onTimeDelivery; }
    public StringProperty notesProperty() { return notes; }
    public ObjectProperty<Date> deliveryDateProperty() { return deliveryDate; }

    // Field Getters
    public String getOrderId() { return orderId.get(); }
    public String getSupplierId() { return supplierId.get(); }
    public Date getOrderDate() { return orderDate.get(); }
    public double getTotalAmount() { return totalAmount.get(); }
    public String getStatus() { return status.get(); }
    public int getDeliveryDays() { return deliveryDays.get(); }
    public boolean isOnTimeDelivery() { return onTimeDelivery.get(); }
    public String getNotes() { return notes.get(); }
    public Date getDeliveryDate() { return deliveryDate.get(); }

    // Field Setters
    public void setOrderId(String orderId) { this.orderId.set(orderId); }
    public void setSupplierId(String supplierId) { this.supplierId.set(supplierId); }
    public void setOrderDate(Date orderDate) { this.orderDate.set(orderDate); }
    public void setTotalAmount(double totalAmount) { this.totalAmount.set(totalAmount); }
    public void setStatus(String status) { this.status.set(status); }
    public void setDeliveryDays(int deliveryDays) {
        this.deliveryDays.set(deliveryDays);
        updateDeliveryDate();
    }
    public void setOnTimeDelivery(boolean onTimeDelivery) { this.onTimeDelivery.set(onTimeDelivery); }
    public void setNotes(String notes) { this.notes.set(notes); }

    /**
     * Updates the delivery date based on the order date and delivery days
     */
    private void updateDeliveryDate() {
        Date orderDate = getOrderDate();
        if (orderDate != null) {
            Date newDeliveryDate = new Date(orderDate.getTime() + (getDeliveryDays() * 86400000L)); // 86400000 = ms in a day
            this.deliveryDate.set(newDeliveryDate);
        }
    }

    /**
     * Generate a new order ID using a timestamp-based format
     * @return Formatted order ID
     */
    public static String generateOrderId() {
        return "ORD-" + System.currentTimeMillis();
    }

    /**
     * Marks an order as completed and calculates if it was delivered on time
     * @param actualDeliveryDate The date when the order was actually delivered
     * @return true if delivered on time, false otherwise
     */
    public boolean completeOrder(Date actualDeliveryDate) {
        setStatus("Completed");

        // Calculate if the delivery was on time
        Date expectedDelivery = getDeliveryDate();
        boolean isOnTime = expectedDelivery != null &&
                (actualDeliveryDate.compareTo(expectedDelivery) <= 0);

        setOnTimeDelivery(isOnTime);
        return isOnTime;
    }

    @Override
    public String toString() {
        return "Order #" + getOrderId() + " - " + getStatus() +
                " (Amount: $" + String.format("%.2f", getTotalAmount()) + ")";
    }
}
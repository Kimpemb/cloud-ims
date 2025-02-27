package com.joshuawilliams.ims.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private int orderId;
    private Customer customer;
    private List<Product> products;
    private List<Integer> quantities;
    private double totalAmount;
    private LocalDateTime orderDate;
    private String processedBy; // Name of the employee who processed the order
    private int processedById; // ID of the employee

    public Order(int orderId, Customer customer, List<Product> products, List<Integer> quantities,
                 double totalAmount, LocalDateTime orderDate, String processedBy, int processedById) {
        this.orderId = orderId;
        this.customer = customer;
        this.products = products;
        this.quantities = quantities;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.processedBy = processedBy;
        this.processedById = processedById;
    }

    // Getters and setters

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Integer> getQuantities() {
        return quantities;
    }

    public void setQuantities(List<Integer> quantities) {
        this.quantities = quantities;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }

    public int getProcessedById() {
        return processedById;
    }

    public void setProcessedById(int processedById) {
        this.processedById = processedById;
    }
}

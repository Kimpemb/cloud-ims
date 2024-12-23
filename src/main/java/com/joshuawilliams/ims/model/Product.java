package com.joshuawilliams.ims.model;

import java.util.Objects;

public class Product {

    private int id;
    private String name;
    private double price;
    private int quantity;
    private int categoryId;

    // Constructor with 5 arguments
    public Product(int id, String name, double price, int quantity, int categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
    }

    // Constructor with 4 arguments (no id)
    public Product(String name, double price, int quantity, int categoryId) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
    }

    // No-argument constructor
    public Product() {
        // Default constructor
    }

    // Setters and getters for each property
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getCategoryId() {
        return categoryId;
    }

    // Add toString method for easy display
    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", price=" + price + ", quantity=" + quantity + ", categoryId=" + categoryId + "]";
    }

    // Override equals and hashCode for better handling in collections and comparisons
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return id == product.id &&
                Double.compare(product.price, price) == 0 &&
                quantity == product.quantity &&
                categoryId == product.categoryId &&
                name.equals(product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, quantity, categoryId);
    }
}

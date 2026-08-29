package com.joshuawilliams.ims.model;

import java.util.Objects;

/**
 * Base class for all sellable products.
 *
 * Made abstract as part of the ecommerce pivot: every real product in the
 * system is one of the concrete subclasses below (Electronics, Clothing,
 * Groceries), each of which knows how to discount itself differently.
 * This is the class hierarchy's root for inheritance + polymorphism.
 */
public abstract class Product {

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

    public void setId(int id) {
        this.id = id;
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

    /**
     * Returns a short label identifying which concrete product type this is
     * (e.g. "ELECTRONICS", "CLOTHING", "GROCERY"). Persisted to the
     * products.product_type column so the DAO knows which subclass to
     * reconstruct when loading rows back out of the database.
     */
    public abstract String getProductType();

    /**
     * Calculates the discount (in currency, not percent) that applies when
     * buying `quantity` units of this product. Each subclass overrides this
     * with its own rule — this is the polymorphism demonstration: calling
     * calculateDiscount() on a mixed list of products runs different logic
     * per object, resolved at runtime based on actual type.
     *
     * @param quantity number of units being purchased
     * @return the discount amount to subtract from (price * quantity)
     */
    public abstract double calculateDiscount(int quantity);

    /**
     * Final price for buying `quantity` units, after this product's own
     * discount rule has been applied. Shared logic, not overridden —
     * subclasses only customize calculateDiscount().
     */
    public double calculateLineTotal(int quantity) {
        double subtotal = price * quantity;
        double discount = calculateDiscount(quantity);
        return Math.max(0.0, subtotal - discount);
    }

    // Add toString method for easy display
    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", price=" + price
                + ", quantity=" + quantity + ", categoryId=" + categoryId
                + ", type=" + getProductType() + "]";
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
package com.joshuawilliams.ims.model;

/**
 * Groceries: flat per-unit discount above a low threshold, modelling
 * everyday bulk-buy behaviour (e.g. buying several of the same food item).
 * Deliberately a different shape of rule (flat amount, not a percentage)
 * from Electronics/Clothing to make the polymorphism visible in output.
 */
public class Groceries extends Product {

    private static final int BULK_THRESHOLD = 4;
    private static final double PER_UNIT_DISCOUNT = 1.50;

    public Groceries(int id, String name, double price, int quantity, int categoryId) {
        super(id, name, price, quantity, categoryId);
    }

    public Groceries(String name, double price, int quantity, int categoryId) {
        super(name, price, quantity, categoryId);
    }

    @Override
    public String getProductType() {
        return "GROCERY";
    }

    @Override
    public double calculateDiscount(int quantity) {
        if (quantity >= BULK_THRESHOLD) {
            return PER_UNIT_DISCOUNT * quantity;
        }
        return 0.0;
    }
}
package com.joshuawilliams.ims.model;

/**
 * Electronics: bulk-purchase discount. Buying several units of the same
 * electronic item (e.g. restocking, gifting) earns a flat 8% off once
 * quantity reaches 3 or more.
 */
public class Electronics extends Product {

    private static final int BULK_THRESHOLD = 3;
    private static final double BULK_DISCOUNT_RATE = 0.08;

    public Electronics(int id, String name, double price, int quantity, int categoryId) {
        super(id, name, price, quantity, categoryId);
    }

    public Electronics(String name, double price, int quantity, int categoryId) {
        super(name, price, quantity, categoryId);
    }

    @Override
    public String getProductType() {
        return "ELECTRONICS";
    }

    @Override
    public double calculateDiscount(int quantity) {
        if (quantity >= BULK_THRESHOLD) {
            return getPrice() * quantity * BULK_DISCOUNT_RATE;
        }
        return 0.0;
    }
}
package com.joshuawilliams.ims.model;

/**
 * Clothing: tiered quantity discount, rewarding larger basket sizes more
 * aggressively than Electronics — clothing has thinner margins per unit but
 * benefits more from moving volume (e.g. multi-item outfits, family buys).
 */
public class Clothing extends Product {

    public Clothing(int id, String name, double price, int quantity, int categoryId) {
        super(id, name, price, quantity, categoryId);
    }

    public Clothing(String name, double price, int quantity, int categoryId) {
        super(name, price, quantity, categoryId);
    }

    @Override
    public String getProductType() {
        return "CLOTHING";
    }

    @Override
    public double calculateDiscount(int quantity) {
        double rate;
        if (quantity >= 5) {
            rate = 0.15;
        } else if (quantity >= 2) {
            rate = 0.05;
        } else {
            rate = 0.0;
        }
        return getPrice() * quantity * rate;
    }
}
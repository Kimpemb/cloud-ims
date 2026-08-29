package com.joshuawilliams.ims.model;

/**
 * Fallback type for products that don't fall into a specific category
 * (matches the DB's product_type default of 'GENERAL'). No discount logic —
 * exists so the factory always has a safe type to construct instead of
 * throwing on unrecognised/legacy rows.
 */
public class GeneralProduct extends Product {

    public GeneralProduct(int id, String name, double price, int quantity, int categoryId) {
        super(id, name, price, quantity, categoryId);
    }

    public GeneralProduct(String name, double price, int quantity, int categoryId) {
        super(name, price, quantity, categoryId);
    }

    @Override
    public String getProductType() {
        return "GENERAL";
    }

    @Override
    public double calculateDiscount(int quantity) {
        return 0.0;
    }
}
package com.joshuawilliams.ims.model;

/**
 * Single source of truth for turning a stored product_type string (plus the
 * row's other fields) into the correct Product subclass. Keeps this
 * decision out of the DAO so ProductDao doesn't need a switch statement
 * repeated in every query method.
 */
public class ProductFactory {

    private ProductFactory() {
        // static factory, no instances
    }

    public static Product create(int id, String name, double price, int quantity,
                                 int categoryId, String productType) {
        if (productType == null) {
            return new GeneralProduct(id, name, price, quantity, categoryId);
        }

        return switch (productType.trim().toUpperCase()) {
            case "ELECTRONICS" -> new Electronics(id, name, price, quantity, categoryId);
            case "CLOTHING" -> new Clothing(id, name, price, quantity, categoryId);
            case "GROCERY" -> new Groceries(id, name, price, quantity, categoryId);
            default -> new GeneralProduct(id, name, price, quantity, categoryId);
        };
    }
}
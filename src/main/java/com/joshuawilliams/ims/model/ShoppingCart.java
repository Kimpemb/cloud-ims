package com.joshuawilliams.ims.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a customer's in-progress selection of products before checkout.
 *
 * The IMS previously had no concept of a pre-checkout cart — Order only ever
 * represented a completed sale. ShoppingCart fills that gap: it accumulates
 * items, and calculateTotal() is where the Product hierarchy's polymorphism
 * becomes visible end-to-end, since it calls calculateLineTotal() on each
 * item without knowing (or caring) which concrete subclass it is.
 */
public class ShoppingCart {

    private final Customer customer;
    // LinkedHashMap keeps items in the order they were added, which matters
    // for a predictable cart UI (items shouldn't jump around as you shop).
    private final Map<Product, Integer> items;

    public ShoppingCart(Customer customer) {
        this.customer = customer;
        this.items = new LinkedHashMap<>();
    }

    public Customer getCustomer() {
        return customer;
    }

    /**
     * Unmodifiable snapshot of the current cart contents (product -> quantity).
     */
    public Map<Product, Integer> getItems() {
        return new LinkedHashMap<>(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getItemCount() {
        int count = 0;
        for (int qty : items.values()) {
            count += qty;
        }
        return count;
    }

    /**
     * Adds a product to the cart. If it's already present, the quantities
     * are combined rather than duplicated as a second line item.
     *
     * @throws IllegalArgumentException if quantity is not positive, or if
     *         the requested quantity (combined with what's already in the
     *         cart) exceeds available stock.
     */
    public void addItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        int existing = items.getOrDefault(product, 0);
        int newTotal = existing + quantity;
        if (newTotal > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Cannot add " + quantity + " of \"" + product.getName()
                            + "\" — only " + (product.getQuantity() - existing) + " more available in stock");
        }
        items.put(product, newTotal);
    }

    /**
     * Sets a product's cart quantity directly (used by a quantity stepper
     * in the UI). A quantity of 0 or less removes the item entirely.
     */
    public void updateItemQuantity(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            items.remove(product);
            return;
        }
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Cannot set quantity to " + quantity + " for \"" + product.getName()
                            + "\" — only " + product.getQuantity() + " in stock");
        }
        items.put(product, quantity);
    }

    public void removeItem(Product product) {
        items.remove(product);
    }

    public void clear() {
        items.clear();
    }

    /**
     * Subtotal before any discounts (price * quantity, summed across items).
     */
    public double calculateSubtotal() {
        double subtotal = 0.0;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            subtotal += entry.getKey().getPrice() * entry.getValue();
        }
        return subtotal;
    }

    /**
     * Total discount across the whole cart. Each product's calculateDiscount()
     * is resolved at runtime based on its actual subclass (Electronics,
     * Clothing, Groceries, or GeneralProduct) — this loop is the clearest
     * single place in the app where that polymorphism is exercised.
     */
    public double calculateTotalDiscount() {
        double totalDiscount = 0.0;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            totalDiscount += entry.getKey().calculateDiscount(entry.getValue());
        }
        return totalDiscount;
    }

    /**
     * Final total after discounts. Sums calculateLineTotal() per item rather
     * than subtotal - totalDiscount, so it stays consistent even if a future
     * subclass overrides calculateLineTotal() itself instead of just
     * calculateDiscount().
     */
    public double calculateTotal() {
        double total = 0.0;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            total += entry.getKey().calculateLineTotal(entry.getValue());
        }
        return total;
    }

    /**
     * Converts this cart into an Order ready to be handed to
     * OrderService.createOrder(). Does not clear the cart or touch the
     * database — that's the service layer's job. The cart is left intact so
     * the caller can clear it only after createOrder() confirms success.
     *
     * @param processedBy   name of the employee/user processing the sale
     * @param processedById id of the employee/user processing the sale
     */
    public Order toOrder(String processedBy, int processedById) {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot check out an empty cart");
        }
        List<Product> products = new ArrayList<>(items.keySet());
        List<Integer> quantities = new ArrayList<>();
        for (Product product : products) {
            quantities.add(items.get(product));
        }
        // totalAmount is recalculated by OrderService.createOrder() from
        // authoritative DB-loaded product state, so 0.0 here is just a
        // placeholder — calculateTotal() above is for cart-preview display.
        return new Order(0, customer, products, quantities, 0.0, LocalDateTime.now(), processedBy, processedById);
    }
}
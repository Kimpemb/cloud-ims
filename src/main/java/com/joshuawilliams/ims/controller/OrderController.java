package com.joshuawilliams.ims.controller;

import com.joshuawilliams.ims.model.Customer;
import com.joshuawilliams.ims.model.Order;
import com.joshuawilliams.ims.model.Product;
import com.joshuawilliams.ims.service.CustomerService;
import com.joshuawilliams.ims.service.OrderService;
import com.joshuawilliams.ims.service.ProductService;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.time.LocalDateTime;
import java.util.List;

public class OrderController {
    private final OrderService orderService;
    private final CustomerService customerService;

    private final TextArea orderSummary;
    private final List<Product> selectedProducts;
    private final List<Integer> selectedQuantities;
    private final ComboBox<Customer> customerComboBox;

    public OrderController(CustomerService customerService, OrderService orderService, TextArea orderSummary,
                           List<Product> selectedProducts, List<Integer> selectedQuantities,
                           ComboBox<Customer> customerComboBox) {
        this.customerService = customerService;
        this.orderService = orderService;
        this.orderSummary = orderSummary;
        this.selectedProducts = selectedProducts;
        this.selectedQuantities = selectedQuantities;
        this.customerComboBox = customerComboBox;
    }

    public void addProduct(ComboBox<Product> productComboBox, TextField quantityField) {
        try {
            Product selectedProduct = productComboBox.getValue();
            int quantity = Integer.parseInt(quantityField.getText());
            if (selectedProduct != null && quantity > 0) {
                selectedProducts.add(selectedProduct);
                selectedQuantities.add(quantity);
                orderSummary.appendText(selectedProduct.getName() + " x " + quantity + "\n");
            }
        } catch (NumberFormatException e) {
            orderSummary.appendText("Invalid quantity input. Please enter a valid number.\n");
        }
    }

    public void submitOrder(ActionEvent event)
    {
        Customer customer = customerComboBox.getValue();
        if (customer != null && !selectedProducts.isEmpty()) {
            double totalPrice = 0.0;
            for (int i = 0; i < selectedProducts.size(); i++) {
                totalPrice += selectedProducts.get(i).getPrice() * selectedQuantities.get(i);
            }

            String currentUserName = "Admin"; // Replace with actual logged-in user's name
            int currentUserId = 1; // Replace with actual logged-in user's ID

            Order order = new Order(0, customer, selectedProducts, selectedQuantities, totalPrice,
                    LocalDateTime.now(), currentUserName, currentUserId);

            if (orderService.createOrder(order)) {
                orderSummary.appendText("Order submitted successfully!\n");
                selectedProducts.clear();
                selectedQuantities.clear();
            } else {
                orderSummary.appendText("Failed to submit order.\n");
            }
        } else {
            orderSummary.appendText("Please select a customer and add at least one product.\n");
        }
    }
}

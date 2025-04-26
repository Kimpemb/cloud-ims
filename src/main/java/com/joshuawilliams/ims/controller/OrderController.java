package com.joshuawilliams.ims.controller;

import com.joshuawilliams.ims.model.Customer;
import com.joshuawilliams.ims.model.Employee;
import com.joshuawilliams.ims.model.Order;
import com.joshuawilliams.ims.model.Product;
import com.joshuawilliams.ims.service.CustomerService;
import com.joshuawilliams.ims.service.OrderService;
import com.joshuawilliams.ims.service.ProductService;
import com.joshuawilliams.ims.utils.SessionManager;
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

    public boolean submitOrder(ActionEvent event) {
        Customer customer = customerComboBox.getValue();
        if (customer != null && !selectedProducts.isEmpty()) {
            double totalPrice = 0.0;
            for (int i = 0; i < selectedProducts.size(); i++) {
                totalPrice += selectedProducts.get(i).getPrice() * selectedQuantities.get(i);
            }

            Employee loggedInEmployee = SessionManager.getLoggedInEmployee();
            String currentUserName = loggedInEmployee != null ? loggedInEmployee.getName() : "Unknown"; // Fetch name from session
            int currentUserId = -1;

            if (loggedInEmployee != null) {
                try {
                    currentUserId = Integer.parseInt(loggedInEmployee.getId()); // Assuming getId() returns a String that can be parsed to int
                } catch (NumberFormatException e) {
                    currentUserId = -1; // Handle case where ID can't be parsed
                }
            }

            Order order = new Order(0, customer, selectedProducts, selectedQuantities, totalPrice,
                    LocalDateTime.now(), currentUserName, currentUserId);

            if (orderService.createOrder(order)) {
                selectedProducts.clear();
                selectedQuantities.clear();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


}

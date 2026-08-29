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
    private final ProductService productService;

    private final TextArea orderSummary;
    private final List<Product> selectedProducts;
    private final List<Integer> selectedQuantities;
    private final ComboBox<Customer> customerComboBox;

    public OrderController(CustomerService customerService, OrderService orderService, ProductService productService,
                           TextArea orderSummary, List<Product> selectedProducts, List<Integer> selectedQuantities,
                           ComboBox<Customer> customerComboBox) {
        this.customerService = customerService;
        this.orderService = orderService;
        this.productService = productService;
        this.orderSummary = orderSummary;
        this.selectedProducts = selectedProducts;
        this.selectedQuantities = selectedQuantities;
        this.customerComboBox = customerComboBox;
    }

    // Method to add product to the order
    public void addProduct(ComboBox<Product> productComboBox, TextField quantityField) {
        try {
            Product selectedProduct = productComboBox.getValue();
            int quantity = Integer.parseInt(quantityField.getText());

            if (selectedProduct != null && quantity > 0) {
                selectedProducts.add(selectedProduct);
                selectedQuantities.add(quantity);
                orderSummary.appendText(selectedProduct.getName() + " x " + quantity + "\n");
            } else {
                orderSummary.appendText("Please select a valid product and quantity.\n");
            }
        } catch (NumberFormatException e) {
            orderSummary.appendText("Invalid quantity input. Please enter a valid number.\n");
        }
    }

    // Method to submit the order
    public boolean submitOrder(ActionEvent event, String processedBy, int processedById) {
        Customer customer = customerComboBox.getValue();
        if (customer != null && !selectedProducts.isEmpty()) {
            double totalPrice = calculateTotalPrice();

            Employee loggedInEmployee = SessionManager.getInstance().getLoggedInEmployee();
            String currentUserName = loggedInEmployee != null ? loggedInEmployee.getName() : "Unknown";
            int currentUserId = (loggedInEmployee != null) ? getEmployeeId(loggedInEmployee) : -1;

            Order order = new Order(0, customer, selectedProducts, selectedQuantities, totalPrice,
                    LocalDateTime.now(), currentUserName, currentUserId);

            boolean orderCreated = orderService.createOrder(order);

            if (orderCreated) {
                resetOrder();
                return true;
            } else {
                return false;
            }
        } else {
            orderSummary.appendText("Please select a customer and add products to the order.\n");
            return false;
        }
    }

    // Helper method to calculate the total price of the order
    private double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (int i = 0; i < selectedProducts.size(); i++) {
            totalPrice += selectedProducts.get(i).getPrice() * selectedQuantities.get(i);
        }
        return totalPrice;
    }

    // Helper method to get the employee ID
    private int getEmployeeId(Employee loggedInEmployee) {
        try {
            return Integer.parseInt(loggedInEmployee.getId());
        } catch (NumberFormatException e) {
            return -1;  // Handle case where ID can't be parsed
        }
    }

    // Helper method to reset the order
    private void resetOrder() {
        selectedProducts.clear();
        selectedQuantities.clear();
        orderSummary.clear();
    }
}

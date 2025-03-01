package com.joshuawilliams.ims.service;


import com.joshuawilliams.ims.dao.OrderDao;
import com.joshuawilliams.ims.model.Customer;
import com.joshuawilliams.ims.model.Order;
import com.joshuawilliams.ims.model.Product;
import com.joshuawilliams.ims.service.CustomerService;
import com.joshuawilliams.ims.service.ProductService;

import java.util.List;

public class OrderService {

    private final OrderDao orderDao;
    private final CustomerService customerService;
    private final ProductService productService;

    public OrderService(OrderDao orderDao, CustomerService customerService, ProductService productService) {
        this.orderDao = orderDao;
        this.customerService = customerService;
        this.productService = productService;
    }

    // Create a new order
    public boolean createOrder(Order order) {
        // Calculate total amount
        double totalAmount = 0.0;
        List<Product> products = order.getProducts();
        List<Integer> quantities = order.getQuantities();

        for (int i = 0; i < products.size(); i++) {
            Product product = productService.getProductById(products.get(i).getId());
            if (product == null || product.getQuantity() < quantities.get(i)) {
                System.out.println("Product not available or insufficient stock: " + products.get(i).getName());
                return false;
            }
            totalAmount += product.getPrice() * quantities.get(i);
        }

        order.setTotalAmount(totalAmount);

        // Update product quantities
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int newQuantity = product.getQuantity() - quantities.get(i);
            product.setQuantity(newQuantity);
            productService.updateProduct(product);
        }

        return orderDao.addOrder(order);
    }

    // Retrieve all orders
    public List<Order> getAllOrders() {
        return orderDao.getAllOrders();
    }

    // OrderService.java
    public int getTotalOrders() {
        return orderDao.getTotalOrders();
    }

    // Retrieve a specific order by ID
    public Order getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    // Delete an order by ID
    public boolean deleteOrder(int orderId) {
        return orderDao.deleteOrder(orderId);
    }
}


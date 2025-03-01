package com.joshuawilliams.ims.controller;

import com.joshuawilliams.ims.service.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardController {

    // Services
    private final ProductService productService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final SupplierService supplierService;
    private final SalesService salesService;
    private final ActivityLogService activityLogService;

    // UI Elements
    public final Label totalProductsLabel;
    public final Label totalEmployeesLabel;
    public final Label totalCustomersLabel;
    public final Label totalOrdersLabel;
    public final Label totalSuppliersLabel;
    public final Label totalSalesLabel;
    public final VBox recentActivitiesBox;
    private final Label chartPlaceholder;
    public final HBox quickActionsBox;

    public DashboardController(
            ProductService productService,
            EmployeeService employeeService,
            CustomerService customerService,
            OrderService orderService,
            SupplierService supplierService,
            SalesService salesService,
            ActivityLogService activityLogService,
            Label totalProductsLabel,
            Label totalEmployeesLabel,
            Label totalCustomersLabel,
            Label totalOrdersLabel,
            Label totalSuppliersLabel,
            Label totalSalesLabel,
            VBox recentActivitiesBox,
            Label chartPlaceholder,
            HBox quickActionsBox
    ) {
        this.productService = productService;
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.supplierService = supplierService;
        this.salesService = salesService;
        this.activityLogService = activityLogService;
        this.totalProductsLabel = totalProductsLabel;
        this.totalEmployeesLabel = totalEmployeesLabel;
        this.totalCustomersLabel = totalCustomersLabel;
        this.totalOrdersLabel = totalOrdersLabel;
        this.totalSuppliersLabel = totalSuppliersLabel;
        this.totalSalesLabel = totalSalesLabel;
        this.recentActivitiesBox = recentActivitiesBox;
        this.chartPlaceholder = chartPlaceholder;
        this.quickActionsBox = quickActionsBox;
    }

    public void initializeDashboard() {
        totalProductsLabel.setText("Total Products: " + productService.getTotalProducts());
        totalEmployeesLabel.setText("Total Employees: " + employeeService.getTotalEmployees());
        totalCustomersLabel.setText("Total Customers: " + customerService.getTotalCustomers());
        totalOrdersLabel.setText("Total Orders: " + orderService.getTotalOrders());
        totalSuppliersLabel.setText("Total Suppliers: " + supplierService.getTotalSuppliers());
        totalSalesLabel.setText("Total Sales Today: $" + salesService.getTodaySales());

        System.out.println("Initializing dashboard...");
        loadRecentActivities();
    }

    private void loadRecentActivities() {
        recentActivitiesBox.getChildren().clear();
        System.out.println("Loading recent activities..."); // Debug line
        activityLogService.getRecentActivities().forEach(activity -> {
            System.out.println("Retrieved Activity: " + activity); // Debug line
            Label activityLabel = new Label(activity);
            recentActivitiesBox.getChildren().add(activityLabel);
        });
    }

}

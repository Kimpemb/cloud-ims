package com.joshuawilliams.ims.ui;

import com.joshuawilliams.ims.controller.DashboardController;
import com.joshuawilliams.ims.service.CustomerService;
import com.joshuawilliams.ims.service.OrderService;
import com.joshuawilliams.ims.service.ProductService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.sql.Connection;

public class DashboardView {

    private final DashboardController dashboardController;
    private final Connection connection;
    private final ProductService productService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final EmployeeManagementView employeeManagementView;
    private final CustomerView customerView;
    private final SupplierView supplierView;
    private final OrderManagementUI orderManagementUI;

    public DashboardView(
            DashboardController dashboardController,
            Connection connection,
            ProductService productService,
            CustomerService customerService,
            OrderService orderService,
            EmployeeManagementView employeeManagementView,
            CustomerView customerView,
            SupplierView supplierView
    ) {
        this.dashboardController = dashboardController;
        this.connection = connection;
        this.productService = productService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.employeeManagementView = employeeManagementView;
        this.customerView = customerView;
        this.supplierView = supplierView;
        this.orderManagementUI = new OrderManagementUI(customerService, productService, orderService);
    }

    public Pane getView() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        root.getChildren().addAll(
                createLabel("Dashboard", 24, true),
                new Separator(),
                createKeyMetricsGrid(),
                new Separator(),
                createLabel("Quick Actions", 18, true),
                createQuickActionsBox(),
                new Separator(),
                createLabel("Recent Activities", 18, true),
                dashboardController.recentActivitiesBox // Ensure recent activities are displayed
        );

        dashboardController.initializeDashboard(); // Load data

        return root;
    }

    private Label createLabel(String text, int fontSize, boolean bold) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: " + fontSize + "px; " + (bold ? "-fx-font-weight: bold;" : ""));
        return label;
    }

    private GridPane createKeyMetricsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);

        grid.add(dashboardController.totalProductsLabel, 0, 0);
        grid.add(dashboardController.totalEmployeesLabel, 1, 0);
        grid.add(dashboardController.totalCustomersLabel, 2, 0);
        grid.add(dashboardController.totalOrdersLabel, 0, 1);
        grid.add(dashboardController.totalSuppliersLabel, 1, 1);
        grid.add(dashboardController.totalSalesLabel, 2, 1);

        return grid;
    }

    private HBox createQuickActionsBox() {
        HBox quickActionsBox = dashboardController.quickActionsBox;
        quickActionsBox.getChildren().setAll(
                createActionButton("Add Product", () -> AddProductDialog.show(new Stage(), connection, (name, price, quantity, categoryId) -> {})),
                createActionButton("Add Order", () -> orderManagementUI.showOrderForm(new Stage())),
                createActionButton("Add Employee", () -> employeeManagementView.showAddEmployeeDialog(new Stage())),
                createActionButton("Add Customer", () -> {
                    Window owner = quickActionsBox.getScene() != null ? quickActionsBox.getScene().getWindow() : null;
                    customerView.createAddCustomerDialog(owner);
                }),
                createActionButton("Add Supplier", () -> supplierView.showSupplierDialog(null)) // Pass null for a new supplier
        );
        return quickActionsBox;
    }

    private Button createActionButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(e -> action.run());
        return button;
    }
}

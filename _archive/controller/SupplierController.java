package com.joshuawilliams.ims.controller;

import com.joshuawilliams.ims.model.Supplier;
import com.joshuawilliams.ims.model.SupplierProductRelation;
import com.joshuawilliams.ims.service.SupplierService;
import com.joshuawilliams.ims.ui.SupplierView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class SupplierController implements Initializable {
    private SupplierService service;
    private SupplierView view;

    public SupplierController(SupplierService service, SupplierView view) {
        this.service = service;
        this.view = view;
        this.view.setController(this);
    }

    public void setSupplierView(SupplierView view) {
        this.view = view;
        this.view.setController(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshSuppliers();
        loadCategories();
    }

    public void onAddSupplier() {
        view.showAddSupplierDialog().ifPresent(supplier -> {
            try {
                if (service.addSupplier(supplier) != null) {
                    view.showSuccess("Supplier added successfully");
                    refreshSuppliers();
                }
            } catch (Exception e) {
                view.showError("Add Supplier Failed", e.getMessage());
            }
        });
    }

    public void onEditSupplier() {
        Supplier selected = view.getSelectedSupplier();
        if (selected != null) {
            view.showEditSupplierDialog(selected).ifPresent(updatedSupplier -> {
                try {
                    if (service.updateSupplier(updatedSupplier)) {
                        view.showSuccess("Supplier updated");
                        refreshSuppliers();
                    }
                } catch (Exception e) {
                    view.showError("Update Failed", e.getMessage());
                }
            });
        }
    }

    public void onDeleteSupplier() {
        Supplier selected = view.getSelectedSupplier();
        if (selected != null && view.confirmAction(
                "Confirm Delete",
                "Delete supplier " + selected.getSupplierName() + "?")) {

            try {
                if (service.deleteSupplier(selected.getSupplierId())) {
                    view.showSuccess("Supplier deleted successfully");
                    refreshSuppliers();
                }
            } catch (SQLException e) {
                view.showError("Delete Failed", e.getMessage());
            }
        }
    }

    public void onToggleStatus() {
        Supplier selected = view.getSelectedSupplier();
        if (selected != null) {
            try {
                if (service.toggleSupplierStatus(selected.getSupplierId())) {
                    view.showSuccess("Status updated successfully");
                    refreshSuppliers();
                }
            } catch (SQLException e) {
                view.showError("Status Update Failed", e.getMessage());
            }
        }
    }

    public void onManageProducts() {
        Supplier selected = view.getSelectedSupplier();
        if (selected != null) {
            try {
                view.showProductRelations(
                        selected,
                        service.getSupplierProducts(selected.getSupplierId())
                );
            } catch (SQLException e) {
                view.showError("Load Products Failed", e.getMessage());
            }
        }
    }

    public void onViewMetrics() {
        Supplier selected = view.getSelectedSupplier();
        if (selected != null) {
            try {
                view.showMetrics(
                        service.calculateSupplierMetrics(selected.getSupplierId())
                );
            } catch (SQLException e) {
                view.showError("Load Metrics Failed", e.getMessage());
            }
        }
    }

    public void onExport() {
        Supplier selected = view.getSelectedSupplier();
        try {
            File exportFile = view.showFileChooser(
                    selected != null ?
                            "Export Supplier Data" : "Export All Suppliers"
            );

            if (exportFile != null) {
                String exportType = view.getExportFormat();
                if (selected != null) {
                    service.exportSupplierData(selected.getSupplierId(), exportFile, exportType);
                } else {
                    service.exportAllSuppliers(exportFile, exportType);
                }
                view.showSuccess("Export completed successfully");
            }
        } catch (Exception e) {
            view.showError("Export Failed", e.getMessage());
        }
    }

    public void onSearch(Map<String, Object> searchParams) {
        try {
            if (searchParams != null && !searchParams.isEmpty()) {
                List<Supplier> allSuppliers = service.getAllSuppliers();
                ObservableList<Supplier> searchResults = FXCollections.observableArrayList();

                String searchTerm = searchParams.get("searchTerm") != null ?
                        searchParams.get("searchTerm").toString().toLowerCase() : "";

                if (!searchTerm.isEmpty()) {
                    for (Supplier supplier : allSuppliers) {
                        if (supplier.getSupplierName().toLowerCase().contains(searchTerm) ||
                                supplier.getEmailAddress().toLowerCase().contains(searchTerm) ||
                                supplier.getPhoneNumber().contains(searchTerm)) {
                            searchResults.add(supplier);
                        }
                    }
                } else {
                    searchResults.addAll(allSuppliers);
                }

                view.displaySuppliers(searchResults);
                view.showSuccess("Search completed. Found " + searchResults.size() + " results.");
            } else {
                refreshSuppliers();
            }
        } catch (SQLException e) {
            view.showError("Search Failed", e.getMessage());
        }
    }

    public void onViewOrderHistory() {
        Supplier selected = view.getSelectedSupplier();
        if (selected != null) {
            try {
                Map<String, Object> metrics = service.calculateSupplierMetrics(selected.getSupplierId());

                StringBuilder orderInfo = new StringBuilder();
                orderInfo.append("Order History for: ").append(selected.getSupplierName()).append("\n");
                orderInfo.append("Total Orders: ").append(metrics.getOrDefault("totalOrders", "N/A")).append("\n");
                orderInfo.append("Total Value: ").append(metrics.getOrDefault("totalValue", "N/A")).append("\n");
                orderInfo.append("Last Order Date: ").append(metrics.getOrDefault("lastOrderDate", "N/A"));

                view.showSuccess(orderInfo.toString());

            } catch (SQLException e) {
                view.showError("Load Order History Failed", e.getMessage());
            }
        } else {
            view.showError("No Selection", "Please select a supplier to view order history.");
        }
    }

    public void onRefresh() {
        try {
            refreshSuppliers();
            loadCategories();
            view.showSuccess("Data refreshed successfully");
        } catch (Exception e) {
            view.showError("Refresh Failed", e.getMessage());
        }
    }

    private ObservableList<Supplier> convertToObservableList(List<Supplier> suppliers) {
        return FXCollections.observableArrayList(suppliers);
    }

    private void refreshSuppliers() {
        try {
            ObservableList<Supplier> suppliers = convertToObservableList(service.getAllSuppliers());
            view.displaySuppliers(suppliers);
        } catch (SQLException e) {
            view.showError("Refresh Failed", e.getMessage());
        }
    }

    private void loadCategories() {
        try {
            view.updateCategoryFilters(service.getAllCategories());
        } catch (SQLException e) {
            view.showError("Load Categories Failed", e.getMessage());
        }
    }
}
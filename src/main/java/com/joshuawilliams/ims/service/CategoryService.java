package com.joshuawilliams.ims.service;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import com.joshuawilliams.ims.dao.CategoryDao;
import com.joshuawilliams.ims.model.Category;

import java.sql.Connection;
import java.util.List;

public class CategoryService {

    private CategoryDao categoryDao;

    public CategoryService(Connection connection) {
        this.categoryDao = new CategoryDao(connection);
    }

    public void loadCategoriesIntoDropdown(ComboBox<Category> categoryDropdown) {
        List<Category> categories = categoryDao.getAllCategories();  // Fetch categories from DAO

        categoryDropdown.getItems().clear();  // Clear existing items

        if (categories != null && !categories.isEmpty()) {
            categoryDropdown.getItems().addAll(categories);  // Add categories to dropdown
        }

        // Customize how items are displayed in the dropdown
        categoryDropdown.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName());  // Display category name
                }
            }
        });

        // Customize the button cell
        categoryDropdown.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName());  // Display category name in the dropdown button
                }
            }
        });
    }
}

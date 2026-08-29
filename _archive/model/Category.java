package com.joshuawilliams.ims.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Category {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;

    // Default constructor
    public Category() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
    }

    // Parameterized constructor
    public Category(int id, String name) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
    }

    // Getters and setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    // Property methods
    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    // Optional: Override toString for better display in UI components
    @Override
    public String toString() {
        return name.get();
    }
}

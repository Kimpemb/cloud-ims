package com.joshuawilliams.ims.model;

public class Department {
    private String id;
    private String name;
    private String code;
    private String description; // Added description field
    private String managerName;
    private String email;
    private String location;
    private String status;

    // Constructors
    public Department() {}

    public Department(String id, String name, String code, String description, String managerName, String email, String location, String status) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.managerName = managerName;
        this.email = email;
        this.location = location;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {  // Added getter for description
        return description;
    }

    public void setDescription(String description) {  // Added setter for description
        this.description = description;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

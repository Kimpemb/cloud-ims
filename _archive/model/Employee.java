package com.joshuawilliams.ims.model;

import java.sql.Date; // Import java.sql.Date
import javafx.beans.property.*;
public class Employee {
    private StringProperty id;
    private StringProperty name;
    private StringProperty role;
    private StringProperty department;
    private StringProperty phoneNumber;
    private StringProperty email;
    private StringProperty password;
    private StringProperty status;
    private ObjectProperty<Date> dateOfBirth; // java.sql.Date
    private ObjectProperty<Date> hireDate;
    private StringProperty address;
    private StringProperty managerId;
    private DoubleProperty salary;
    private StringProperty performanceReview;
    private StringProperty employmentType;
    private StringProperty emergencyContact;
    private StringProperty nationalId;

    // Constructor with all fields
    public Employee(String id, String name, String role, String department, String phoneNumber, String email,
                    String status, Date dateOfBirth, Date hireDate, String address, String managerId,
                    double salary, String performanceReview, String employmentType, String emergencyContact,
                    String nationalId, String password) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.role = new SimpleStringProperty(role);
        this.department = new SimpleStringProperty(department);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.email = new SimpleStringProperty(email);
        this.status = new SimpleStringProperty(status);
        this.dateOfBirth = new SimpleObjectProperty<>(dateOfBirth);
        this.hireDate = new SimpleObjectProperty<>(hireDate);
        this.address = new SimpleStringProperty(address);
        this.managerId = new SimpleStringProperty(managerId);
        this.salary = new SimpleDoubleProperty(salary);
        this.performanceReview = new SimpleStringProperty(performanceReview);
        this.employmentType = new SimpleStringProperty(employmentType);
        this.emergencyContact = new SimpleStringProperty(emergencyContact);
        this.nationalId = new SimpleStringProperty(nationalId);
        this.password = new SimpleStringProperty(password); // Added password field
    }


    // No-argument constructor for flexibility in object creation (e.g., frameworks, collections),
// and parameterized constructor for convenient, direct initialization with values.

    public Employee() {
        this.id = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.role = new SimpleStringProperty();
        this.department = new SimpleStringProperty();
        this.phoneNumber = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.password = new SimpleStringProperty(); // For no-argument constructor
        this.status = new SimpleStringProperty();
        this.dateOfBirth = new SimpleObjectProperty<>();
        this.hireDate = new SimpleObjectProperty<>();
        this.address = new SimpleStringProperty();
        this.managerId = new SimpleStringProperty();
        this.salary = new SimpleDoubleProperty();
        this.performanceReview = new SimpleStringProperty();
        this.employmentType = new SimpleStringProperty();
        this.emergencyContact = new SimpleStringProperty();
        this.nationalId = new SimpleStringProperty();
    }
    // Getters and Setters
    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public StringProperty roleProperty() {
        return role;
    }

    public String getDepartment() {
        return department.get();
    }

    public void setDepartment(String department) {
        this.department.set(department);
    }

    public StringProperty departmentProperty() {
        return department;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public Date getDateOfBirth() {  // Return java.sql.Date
        return dateOfBirth.get();
    }

    public void setDateOfBirth(Date dateOfBirth) {  // Accept java.sql.Date
        this.dateOfBirth.set(dateOfBirth);
    }

    public ObjectProperty<Date> dateOfBirthProperty() {
        return dateOfBirth;
    }

    public Date getHireDate() {
        return hireDate.get();
    }

    public void setHireDate(Date hireDate) {
        this.hireDate.set(hireDate);
    }

    public ObjectProperty<Date> hireDateProperty() {
        return hireDate;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
    }

    public String getManagerId() {
        return managerId.get();
    }

    public void setManagerId(String managerId) {
        this.managerId.set(managerId);
    }

    public StringProperty managerIdProperty() {
        return managerId;
    }

    public double getSalary() {
        return salary.get();
    }

    public void setSalary(double salary) {
        this.salary.set(salary);
    }

    public DoubleProperty salaryProperty() {
        return salary;
    }

    public String getPerformanceReview() {
        return performanceReview.get();
    }

    public void setPerformanceReview(String performanceReview) {
        this.performanceReview.set(performanceReview);
    }

    public StringProperty performanceReviewProperty() {
        return performanceReview;
    }

    public String getEmploymentType() {
        return employmentType.get();
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType.set(employmentType);
    }

    public StringProperty employmentTypeProperty() {
        return employmentType;
    }

    public String getEmergencyContact() {
        return emergencyContact.get();
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact.set(emergencyContact);
    }

    public StringProperty emergencyContactProperty() {
        return emergencyContact;
    }

    public String getNationalId() {
        return nationalId.get();
    }

    public void setNationalId(String nationalId) {
        this.nationalId.set(nationalId);
    }

    public StringProperty nationalIdProperty() {
        return nationalId;
    }
}

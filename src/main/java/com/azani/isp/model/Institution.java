package com.azani.isp.model;

import java.time.LocalDate;

/**
 * Abstract Base Class representing a Learning Institution subscribing to Azani ISP.
 * Demonstrates Abstraction and Encapsulation in OOP.
 */
public abstract class Institution {
    public static final double REGISTRATION_FEE = 8500.0;
    public static final double BASE_INSTALLATION_FEE = 10000.0;

    private int id;
    private String name;
    private InstitutionCategory category;
    private String physicalAddress;
    private LocalDate registrationDate;
    private String status; // ACTIVE, DEFAULTER, DISCONNECTED, PENDING_INSTALLATION
    private ContactPerson contactPerson;
    private Infrastructure infrastructure;
    private Subscription subscription;

    public Institution() {
        this.registrationDate = LocalDate.now();
        this.status = "ACTIVE";
    }

    public Institution(int id, String name, InstitutionCategory category, String physicalAddress, LocalDate registrationDate, String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.physicalAddress = physicalAddress;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    public Institution(String name, InstitutionCategory category, String physicalAddress, LocalDate registrationDate) {
        this.name = name;
        this.category = category;
        this.physicalAddress = physicalAddress;
        this.registrationDate = registrationDate;
        this.status = "ACTIVE";
    }

    // Abstract methods demonstrating Polymorphism
    public abstract String getRecommendedBandwidth();
    public abstract String getCategoryDescription();

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InstitutionCategory getCategory() {
        return category;
    }

    protected void setCategory(InstitutionCategory category) {
        this.category = category;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ContactPerson getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(ContactPerson contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Infrastructure getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(Infrastructure infrastructure) {
        this.infrastructure = infrastructure;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return "[" + category.getDisplayName() + "] " + name + " (ID: " + id + ", Status: " + status + ")";
    }
}

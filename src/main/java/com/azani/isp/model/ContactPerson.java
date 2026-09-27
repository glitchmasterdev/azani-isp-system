package com.azani.isp.model;

/**
 * Represents the personal details of the contact person representing an institution.
 */
public class ContactPerson {
    private int id;
    private int institutionId;
    private String fullName;
    private String designation;
    private String phone;
    private String email;
    private String nationalId;

    public ContactPerson() {}

    public ContactPerson(String fullName, String designation, String phone, String email, String nationalId) {
        this.fullName = fullName;
        this.designation = designation;
        this.phone = phone;
        this.email = email;
        this.nationalId = nationalId;
    }

    public ContactPerson(int id, int institutionId, String fullName, String designation, String phone, String email, String nationalId) {
        this.id = id;
        this.institutionId = institutionId;
        this.fullName = fullName;
        this.designation = designation;
        this.phone = phone;
        this.email = email;
        this.nationalId = nationalId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(int institutionId) {
        this.institutionId = institutionId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    @Override
    public String toString() {
        return fullName + " (" + designation + ", Tel: " + phone + ", Email: " + email + ", ID: " + nationalId + ")";
    }
}

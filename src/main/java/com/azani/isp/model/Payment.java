package com.azani.isp.model;

import java.time.LocalDateTime;

/**
 * Represents a payment transaction captured in the system.
 */
public class Payment {
    private int id;
    private int institutionId;
    private PaymentType paymentType;
    private double amount;
    private LocalDateTime paymentDate;
    private String referenceNo;
    private String notes;

    public Payment() {
        this.paymentDate = LocalDateTime.now();
    }

    public Payment(int institutionId, PaymentType paymentType, double amount, String referenceNo, String notes) {
        this.institutionId = institutionId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.paymentDate = LocalDateTime.now();
        this.referenceNo = referenceNo;
        this.notes = notes;
    }

    public Payment(int institutionId, PaymentType paymentType, double amount, LocalDateTime paymentDate, String referenceNo, String notes) {
        this.institutionId = institutionId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.paymentDate = paymentDate != null ? paymentDate : LocalDateTime.now();
        this.referenceNo = referenceNo;
        this.notes = notes;
    }

    public Payment(int id, int institutionId, PaymentType paymentType, double amount, LocalDateTime paymentDate, String referenceNo, String notes) {
        this.id = id;
        this.institutionId = institutionId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.referenceNo = referenceNo;
        this.notes = notes;
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

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return referenceNo + " | " + paymentType + " | KSh " + String.format("%,.2f", amount) + " | " + paymentDate;
    }
}

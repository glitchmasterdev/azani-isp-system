package com.azani.isp.model;

/**
 * Encapsulates financial breakdown by institution category (SCO200 Req 4(d)).
 */
public class CategoryFinancialSummary {
    private final InstitutionCategory category;
    private int institutionCount;
    private double totalMonthlyCharges;
    private double totalOverdueFines;
    private double totalReconnectionFees;
    private double grandTotal;

    public CategoryFinancialSummary(InstitutionCategory category) {
        this.category = category;
        this.institutionCount = 0;
        this.totalMonthlyCharges = 0.0;
        this.totalOverdueFines = 0.0;
        this.totalReconnectionFees = 0.0;
        this.grandTotal = 0.0;
    }

    public void addRecord(double monthlyCharge, double overdueFine, double reconnectionFee) {
        this.institutionCount++;
        this.totalMonthlyCharges += monthlyCharge;
        this.totalOverdueFines += overdueFine;
        this.totalReconnectionFees += reconnectionFee;
        this.grandTotal = this.totalMonthlyCharges + this.totalOverdueFines + this.totalReconnectionFees;
    }

    public InstitutionCategory getCategory() {
        return category;
    }

    public int getInstitutionCount() {
        return institutionCount;
    }

    public double getTotalMonthlyCharges() {
        return totalMonthlyCharges;
    }

    public double getTotalOverdueFines() {
        return totalOverdueFines;
    }

    public double getTotalReconnectionFees() {
        return totalReconnectionFees;
    }

    public double getGrandTotal() {
        return grandTotal;
    }
}

package com.azani.isp.model;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Represents a monthly billing cycle, tracking charges, overdue fines (15%),
 * disconnections (after 10th of subsequent month), and reconnection fees (KSh 1,000).
 */
public class Bill {
    public static final double OVERDUE_FINE_PERCENT = 15.0; // 15% surcharge
    public static final double RECONNECTION_FEE = 1000.0;   // KSh 1,000

    private int id;
    private int institutionId;
    private int billingMonth;
    private int billingYear;
    private double baseCharge;
    private double overdueFine;
    private double reconnectionFee;
    private double totalDue;
    private double amountPaid;
    private LocalDate dueDate;
    private LocalDate disconnectionDate;
    private String status; // PENDING, PAID, OVERDUE, DISCONNECTED

    public Bill() {}

    public Bill(int institutionId, int billingMonth, int billingYear, double baseCharge) {
        this.institutionId = institutionId;
        this.billingMonth = billingMonth;
        this.billingYear = billingYear;
        this.baseCharge = baseCharge;
        this.overdueFine = 0.0;
        this.reconnectionFee = 0.0;
        this.amountPaid = 0.0;
        this.status = "PENDING";

        YearMonth ym = YearMonth.of(billingYear, billingMonth);
        this.dueDate = ym.atEndOfMonth(); // End of current month
        YearMonth nextYm = ym.plusMonths(1);
        this.disconnectionDate = nextYm.atDay(10); // 10th of subsequent month

        recalculateTotal();
    }

    public Bill(int id, int institutionId, int billingMonth, int billingYear, double baseCharge,
                double overdueFine, double reconnectionFee, double totalDue, double amountPaid,
                LocalDate dueDate, LocalDate disconnectionDate, String status) {
        this.id = id;
        this.institutionId = institutionId;
        this.billingMonth = billingMonth;
        this.billingYear = billingYear;
        this.baseCharge = baseCharge;
        this.overdueFine = overdueFine;
        this.reconnectionFee = reconnectionFee;
        this.totalDue = totalDue;
        this.amountPaid = amountPaid;
        this.dueDate = dueDate;
        this.disconnectionDate = disconnectionDate;
        this.status = status;
    }

    public void recalculateTotal() {
        this.totalDue = this.baseCharge + this.overdueFine + this.reconnectionFee;
    }

    /**
     * Evaluates billing status based on an evaluation date:
     * - If evaluation date > dueDate and unpaid -> status OVERDUE, fine 15% applied
     * - If evaluation date > disconnectionDate and unpaid -> status DISCONNECTED
     */
    public void evaluateStatus(LocalDate evaluationDate) {
        if ("PAID".equalsIgnoreCase(this.status)) {
            return;
        }

        if (evaluationDate.isAfter(this.disconnectionDate)) {
            this.status = "DISCONNECTED";
            this.overdueFine = this.baseCharge * (OVERDUE_FINE_PERCENT / 100.0);
            recalculateTotal();
        } else if (evaluationDate.isAfter(this.dueDate)) {
            this.status = "OVERDUE";
            this.overdueFine = this.baseCharge * (OVERDUE_FINE_PERCENT / 100.0);
            recalculateTotal();
        } else {
            this.status = "PENDING";
            this.overdueFine = 0.0;
            recalculateTotal();
        }
    }

    /**
     * Applies re-connection surcharge if an institution was disconnected.
     */
    public void applyReconnectionFee() {
        this.reconnectionFee = RECONNECTION_FEE;
        recalculateTotal();
    }

    // Getters and Setters
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

    public int getBillingMonth() {
        return billingMonth;
    }

    public int getBillingYear() {
        return billingYear;
    }

    public double getBaseCharge() {
        return baseCharge;
    }

    public void setBaseCharge(double baseCharge) {
        this.baseCharge = baseCharge;
        recalculateTotal();
    }

    public double getOverdueFine() {
        return overdueFine;
    }

    public void setOverdueFine(double overdueFine) {
        this.overdueFine = overdueFine;
        recalculateTotal();
    }

    public double getReconnectionFee() {
        return reconnectionFee;
    }

    public void setReconnectionFee(double reconnectionFee) {
        this.reconnectionFee = reconnectionFee;
        recalculateTotal();
    }

    public double getTotalDue() {
        return totalDue;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getDisconnectionDate() {
        return disconnectionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getOutstandingBalance() {
        return Math.max(0.0, totalDue - amountPaid);
    }

    @Override
    public String toString() {
        return "Bill [M/Y: " + billingMonth + "/" + billingYear + ", Base: KSh " + String.format("%,.2f", baseCharge) +
                ", Fine (15%): KSh " + String.format("%,.2f", overdueFine) +
                ", Reconnect: KSh " + String.format("%,.2f", reconnectionFee) +
                ", Total: KSh " + String.format("%,.2f", totalDue) + ", Status: " + status + "]";
    }
}

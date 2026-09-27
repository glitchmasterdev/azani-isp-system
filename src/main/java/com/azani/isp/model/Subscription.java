package com.azani.isp.model;

import java.time.LocalDate;

/**
 * Represents an institution's active internet subscription package.
 */
public class Subscription {
    private int id;
    private int institutionId;
    private BandwidthPackage bandwidthPackage;
    private double baseMonthlyCost;
    private boolean upgraded;
    private double discountPercent;
    private double finalMonthlyCost;
    private LocalDate startDate;
    private boolean active;

    public Subscription() {
        this.startDate = LocalDate.now();
        this.active = true;
    }

    public Subscription(int institutionId, BandwidthPackage bandwidthPackage, boolean upgraded, LocalDate startDate) {
        this.institutionId = institutionId;
        this.bandwidthPackage = bandwidthPackage;
        this.upgraded = upgraded;
        this.startDate = startDate != null ? startDate : LocalDate.now();
        this.active = true;
        recalculateCost();
    }

    public Subscription(int id, int institutionId, BandwidthPackage bandwidthPackage, double baseMonthlyCost,
                        boolean upgraded, double discountPercent, double finalMonthlyCost, LocalDate startDate, boolean active) {
        this.id = id;
        this.institutionId = institutionId;
        this.bandwidthPackage = bandwidthPackage;
        this.baseMonthlyCost = baseMonthlyCost;
        this.upgraded = upgraded;
        this.discountPercent = discountPercent;
        this.finalMonthlyCost = finalMonthlyCost;
        this.startDate = startDate;
        this.active = active;
    }

    public void recalculateCost() {
        if (bandwidthPackage != null) {
            this.baseMonthlyCost = bandwidthPackage.getMonthlyCost();
            if (upgraded) {
                this.discountPercent = BandwidthPackage.UPGRADE_DISCOUNT_PERCENT; // 10%
                this.finalMonthlyCost = baseMonthlyCost * (1.0 - (discountPercent / 100.0));
            } else {
                this.discountPercent = 0.0;
                this.finalMonthlyCost = baseMonthlyCost;
            }
        }
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

    public BandwidthPackage getBandwidthPackage() {
        return bandwidthPackage;
    }

    public void setBandwidthPackage(BandwidthPackage bandwidthPackage) {
        this.bandwidthPackage = bandwidthPackage;
        recalculateCost();
    }

    public double getBaseMonthlyCost() {
        return baseMonthlyCost;
    }

    public boolean isUpgraded() {
        return upgraded;
    }

    public void setUpgraded(boolean upgraded) {
        this.upgraded = upgraded;
        recalculateCost();
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public double getFinalMonthlyCost() {
        return finalMonthlyCost;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return bandwidthPackage.getLabel() + (upgraded ? " [UPGRADED - 10% OFF]" : "") +
                " -> KSh " + String.format("%,.2f", finalMonthlyCost) + "/mo";
    }
}

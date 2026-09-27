package com.azani.isp.model;

/**
 * Model representing the aggregated financial amounts for each service category,
 * grouped per institution (SCO200 Req 4(e)).
 */
public class ServiceAggregateSummary {
    private int institutionId;
    private String institutionName;
    private InstitutionCategory category;
    private double registrationFee;
    private double installationBaseFee;
    private double pcEquipmentCost;
    private double lanNodesCost;
    private double monthlyInternetCharges;
    private double overdueFines;
    private double reconnectionFees;
    private double totalAggregateAmount;

    public ServiceAggregateSummary(int institutionId, String institutionName, InstitutionCategory category) {
        this.institutionId = institutionId;
        this.institutionName = institutionName;
        this.category = category;
        this.registrationFee = Institution.REGISTRATION_FEE;
    }

    public void recalculateTotal() {
        this.totalAggregateAmount = registrationFee + installationBaseFee + pcEquipmentCost +
                lanNodesCost + monthlyInternetCharges + overdueFines + reconnectionFees;
    }

    public int getInstitutionId() {
        return institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public InstitutionCategory getCategory() {
        return category;
    }

    public double getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(double registrationFee) {
        this.registrationFee = registrationFee;
        recalculateTotal();
    }

    public double getInstallationBaseFee() {
        return installationBaseFee;
    }

    public void setInstallationBaseFee(double installationBaseFee) {
        this.installationBaseFee = installationBaseFee;
        recalculateTotal();
    }

    public double getPcEquipmentCost() {
        return pcEquipmentCost;
    }

    public void setPcEquipmentCost(double pcEquipmentCost) {
        this.pcEquipmentCost = pcEquipmentCost;
        recalculateTotal();
    }

    public double getLanNodesCost() {
        return lanNodesCost;
    }

    public void setLanNodesCost(double lanNodesCost) {
        this.lanNodesCost = lanNodesCost;
        recalculateTotal();
    }

    public double getMonthlyInternetCharges() {
        return monthlyInternetCharges;
    }

    public void setMonthlyInternetCharges(double monthlyInternetCharges) {
        this.monthlyInternetCharges = monthlyInternetCharges;
        recalculateTotal();
    }

    public double getOverdueFines() {
        return overdueFines;
    }

    public void setOverdueFines(double overdueFines) {
        this.overdueFines = overdueFines;
        recalculateTotal();
    }

    public double getReconnectionFees() {
        return reconnectionFees;
    }

    public void setReconnectionFees(double reconnectionFees) {
        this.reconnectionFees = reconnectionFees;
        recalculateTotal();
    }

    public double getTotalAggregateAmount() {
        return totalAggregateAmount;
    }
}

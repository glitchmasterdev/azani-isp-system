package com.azani.isp.model;

/**
 * Encapsulates the infrastructure requirements and readiness of an institution.
 * Handles computation of PC costs, LAN nodes bracket costs, and total installation costs.
 */
public class Infrastructure {
    public static final double PC_UNIT_PRICE = 40000.0;
    public static final double BASE_INSTALLATION_FEE = 10000.0;

    private int id;
    private int institutionId;
    private int numUsers;
    private boolean ready;
    private int pcsPurchased;
    private int lanNodes;
    private double pcCost;
    private double lanCost;
    private double baseInstallationFee;
    private double totalInstallationCost;

    public Infrastructure() {
        this.baseInstallationFee = BASE_INSTALLATION_FEE;
    }

    public Infrastructure(int institutionId, int numUsers, boolean ready, int pcsPurchased, int lanNodes) {
        this.institutionId = institutionId;
        this.numUsers = numUsers;
        this.ready = ready;
        this.pcsPurchased = ready ? 0 : Math.max(0, pcsPurchased);
        this.lanNodes = ready ? 0 : Math.max(0, lanNodes);
        this.baseInstallationFee = BASE_INSTALLATION_FEE;
        recalculateCosts();
    }

    public Infrastructure(int id, int institutionId, int numUsers, boolean ready, int pcsPurchased, int lanNodes,
                          double pcCost, double lanCost, double baseInstallationFee, double totalInstallationCost) {
        this.id = id;
        this.institutionId = institutionId;
        this.numUsers = numUsers;
        this.ready = ready;
        this.pcsPurchased = pcsPurchased;
        this.lanNodes = lanNodes;
        this.pcCost = pcCost;
        this.lanCost = lanCost;
        this.baseInstallationFee = baseInstallationFee;
        this.totalInstallationCost = totalInstallationCost;
    }

    /**
     * Calculates the cost of LAN nodes according to Table 2:
     * 2 - 10:  KSh 10,000
     * 11 - 20: KSh 20,000
     * 21 - 40: KSh 30,000
     * 41 - 100: KSh 40,000
     */
    public static double calculateLanCost(int nodes) {
        if (nodes <= 0) {
            return 0.0;
        } else if (nodes <= 10) {
            return 10000.0;
        } else if (nodes <= 20) {
            return 20000.0;
        } else if (nodes <= 40) {
            return 30000.0;
        } else if (nodes <= 100) {
            return 40000.0;
        } else {
            // For over 100 nodes, scale based on 40,000 per 100 block
            int blocks = (int) Math.ceil((double) nodes / 100.0);
            return blocks * 40000.0;
        }
    }

    public void recalculateCosts() {
        if (ready) {
            this.pcsPurchased = 0;
            this.lanNodes = 0;
            this.pcCost = 0.0;
            this.lanCost = 0.0;
        } else {
            this.pcCost = this.pcsPurchased * PC_UNIT_PRICE;
            this.lanCost = calculateLanCost(this.lanNodes);
        }
        this.baseInstallationFee = BASE_INSTALLATION_FEE;
        this.totalInstallationCost = this.baseInstallationFee + this.pcCost + this.lanCost;
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

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        recalculateCosts();
    }

    public int getPcsPurchased() {
        return pcsPurchased;
    }

    public void setPcsPurchased(int pcsPurchased) {
        this.pcsPurchased = pcsPurchased;
        recalculateCosts();
    }

    public int getLanNodes() {
        return lanNodes;
    }

    public void setLanNodes(int lanNodes) {
        this.lanNodes = lanNodes;
        recalculateCosts();
    }

    public double getPcCost() {
        return pcCost;
    }

    public double getLanCost() {
        return lanCost;
    }

    public double getHardwareCost() {
        return pcCost + lanCost;
    }

    public double getBaseInstallationFee() {
        return baseInstallationFee;
    }

    public double getTotalInstallationCost() {
        return totalInstallationCost;
    }

    @Override
    public String toString() {
        return "Infrastructure [Users: " + numUsers + ", Ready: " + ready +
                ", PCs: " + pcsPurchased + " (KSh " + String.format("%,.2f", pcCost) + ")" +
                ", LAN Nodes: " + lanNodes + " (KSh " + String.format("%,.2f", lanCost) + ")" +
                ", Total Install: KSh " + String.format("%,.2f", totalInstallationCost) + "]";
    }
}

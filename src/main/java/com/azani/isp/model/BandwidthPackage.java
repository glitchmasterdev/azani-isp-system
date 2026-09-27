package com.azani.isp.model;

/**
 * Bandwidth packages offered by Azani ISP according to Table 1.
 */
public enum BandwidthPackage {
    MBPS_4(4, 1200.0, "4 MBPS"),
    MBPS_10(10, 2000.0, "10 MBPS"),
    MBPS_20(20, 3500.0, "20 MBPS"),
    MBPS_25(25, 4000.0, "25 MBPS"),
    MBPS_50(50, 7000.0, "50 MBPS");

    public static final double UPGRADE_DISCOUNT_PERCENT = 10.0;

    private final int speedMbps;
    private final double monthlyCost;
    private final String label;

    BandwidthPackage(int speedMbps, double monthlyCost, String label) {
        this.speedMbps = speedMbps;
        this.monthlyCost = monthlyCost;
        this.label = label;
    }

    public int getSpeedMbps() {
        return speedMbps;
    }

    public double getMonthlyCost() {
        return monthlyCost;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Calculates the monthly fee considering whether the package is an upgrade.
     * Upgrades receive a 10% discount on the bandwidth cost.
     */
    public double calculateMonthlyFee(boolean isUpgraded) {
        if (isUpgraded) {
            return monthlyCost * (1.0 - (UPGRADE_DISCOUNT_PERCENT / 100.0));
        }
        return monthlyCost;
    }

    public static BandwidthPackage fromSpeed(int speed) {
        for (BandwidthPackage pkg : BandwidthPackage.values()) {
            if (pkg.speedMbps == speed) {
                return pkg;
            }
        }
        throw new IllegalArgumentException("Unsupported bandwidth speed: " + speed + " MBPS");
    }

    @Override
    public String toString() {
        return label + " (KSh " + String.format("%,.2f", monthlyCost) + "/mo)";
    }
}

package com.azani.isp.model;

/**
 * Types of payments captured in the Azani ISP Information System.
 */
public enum PaymentType {
    REGISTRATION("Registration Fee"),
    INSTALLATION("Installation Fee"),
    MONTHLY("Monthly Internet Payment"),
    RECONNECTION("Reconnection Fee");

    private final String description;

    PaymentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static PaymentType fromString(String text) {
        for (PaymentType pt : PaymentType.values()) {
            if (pt.name().equalsIgnoreCase(text) || pt.description.equalsIgnoreCase(text)) {
                return pt;
            }
        }
        throw new IllegalArgumentException("Unknown payment type: " + text);
    }

    @Override
    public String toString() {
        return description;
    }
}

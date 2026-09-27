package com.azani.isp.model;

/**
 * Categories of learning institutions serviced by Azani ISP.
 */
public enum InstitutionCategory {
    PRIMARY_SCHOOL("Primary School"),
    JUNIOR_SCHOOL("Junior School"),
    SENIOR_SCHOOL("Senior School"),
    COLLEGE("College");

    private final String displayName;

    InstitutionCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static InstitutionCategory fromString(String text) {
        for (InstitutionCategory cat : InstitutionCategory.values()) {
            if (cat.name().equalsIgnoreCase(text) || cat.displayName.equalsIgnoreCase(text)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Unknown Institution Category: " + text);
    }

    @Override
    public String toString() {
        return displayName;
    }
}

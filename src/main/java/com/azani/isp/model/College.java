package com.azani.isp.model;

import java.time.LocalDate;

/**
 * Concrete class for Colleges and Tertiary Institutions.
 * Demonstrates Inheritance in OOP.
 */
public class College extends Institution {

    public College() {
        super();
        setCategory(InstitutionCategory.COLLEGE);
    }

    public College(int id, String name, String physicalAddress, LocalDate registrationDate, String status) {
        super(id, name, InstitutionCategory.COLLEGE, physicalAddress, registrationDate, status);
    }

    public College(String name, String physicalAddress, LocalDate registrationDate) {
        super(name, InstitutionCategory.COLLEGE, physicalAddress, registrationDate);
    }

    @Override
    public String getRecommendedBandwidth() {
        return "25 MBPS or 50 MBPS (Campus-wide wireless, research, and e-learning portals)";
    }

    @Override
    public String getCategoryDescription() {
        return "Tertiary education college or institute offering diploma and degree programs.";
    }
}

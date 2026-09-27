package com.azani.isp.model;

import java.time.LocalDate;

/**
 * Concrete class for Primary Schools.
 * Demonstrates Inheritance in OOP.
 */
public class PrimarySchool extends Institution {

    public PrimarySchool() {
        super();
        setCategory(InstitutionCategory.PRIMARY_SCHOOL);
    }

    public PrimarySchool(int id, String name, String physicalAddress, LocalDate registrationDate, String status) {
        super(id, name, InstitutionCategory.PRIMARY_SCHOOL, physicalAddress, registrationDate, status);
    }

    public PrimarySchool(String name, String physicalAddress, LocalDate registrationDate) {
        super(name, InstitutionCategory.PRIMARY_SCHOOL, physicalAddress, registrationDate);
    }

    @Override
    public String getRecommendedBandwidth() {
        return "4 MBPS or 10 MBPS (Basic educational and administrative use)";
    }

    @Override
    public String getCategoryDescription() {
        return "Primary learning institution offering fundamental education.";
    }
}

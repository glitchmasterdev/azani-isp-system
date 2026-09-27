package com.azani.isp.model;

import java.time.LocalDate;

/**
 * Concrete class for Junior Schools.
 * Demonstrates Inheritance in OOP.
 */
public class JuniorSchool extends Institution {

    public JuniorSchool() {
        super();
        setCategory(InstitutionCategory.JUNIOR_SCHOOL);
    }

    public JuniorSchool(int id, String name, String physicalAddress, LocalDate registrationDate, String status) {
        super(id, name, InstitutionCategory.JUNIOR_SCHOOL, physicalAddress, registrationDate, status);
    }

    public JuniorSchool(String name, String physicalAddress, LocalDate registrationDate) {
        super(name, InstitutionCategory.JUNIOR_SCHOOL, physicalAddress, registrationDate);
    }

    @Override
    public String getRecommendedBandwidth() {
        return "10 MBPS or 20 MBPS (Intermediate e-learning and computer laboratory use)";
    }

    @Override
    public String getCategoryDescription() {
        return "Junior secondary institution providing middle school education.";
    }
}

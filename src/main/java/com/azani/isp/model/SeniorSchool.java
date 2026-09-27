package com.azani.isp.model;

import java.time.LocalDate;

/**
 * Concrete class for Senior Schools.
 * Demonstrates Inheritance in OOP.
 */
public class SeniorSchool extends Institution {

    public SeniorSchool() {
        super();
        setCategory(InstitutionCategory.SENIOR_SCHOOL);
    }

    public SeniorSchool(int id, String name, String physicalAddress, LocalDate registrationDate, String status) {
        super(id, name, InstitutionCategory.SENIOR_SCHOOL, physicalAddress, registrationDate, status);
    }

    public SeniorSchool(String name, String physicalAddress, LocalDate registrationDate) {
        super(name, InstitutionCategory.SENIOR_SCHOOL, physicalAddress, registrationDate);
    }

    @Override
    public String getRecommendedBandwidth() {
        return "20 MBPS, 25 MBPS, or 50 MBPS (High bandwidth for high school labs and staff)";
    }

    @Override
    public String getCategoryDescription() {
        return "Senior secondary school preparing students for higher education.";
    }
}

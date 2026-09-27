package com.azani.isp.model;

import java.time.LocalDate;

/**
 * Factory class for creating Institution instances based on category.
 * Implements Factory Pattern in OOP.
 */
public class InstitutionFactory {

    public static Institution createInstitution(int id, String name, InstitutionCategory category, String address, LocalDate regDate, String status) {
        switch (category) {
            case PRIMARY_SCHOOL:
                return new PrimarySchool(id, name, address, regDate, status);
            case JUNIOR_SCHOOL:
                return new JuniorSchool(id, name, address, regDate, status);
            case SENIOR_SCHOOL:
                return new SeniorSchool(id, name, address, regDate, status);
            case COLLEGE:
                return new College(id, name, address, regDate, status);
            default:
                throw new IllegalArgumentException("Unsupported institution category: " + category);
        }
    }

    public static Institution createNewInstitution(String name, InstitutionCategory category, String address, LocalDate regDate) {
        switch (category) {
            case PRIMARY_SCHOOL:
                return new PrimarySchool(name, address, regDate);
            case JUNIOR_SCHOOL:
                return new JuniorSchool(name, address, regDate);
            case SENIOR_SCHOOL:
                return new SeniorSchool(name, address, regDate);
            case COLLEGE:
                return new College(name, address, regDate);
            default:
                throw new IllegalArgumentException("Unsupported institution category: " + category);
        }
    }
}

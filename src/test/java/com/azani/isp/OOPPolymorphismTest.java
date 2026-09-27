package com.azani.isp;

import com.azani.isp.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class OOPPolymorphismTest {

    @Test
    @DisplayName("Test Polymorphism across Institution subclasses")
    public void testPolymorphicBehaviors() {
        Institution pri = InstitutionFactory.createNewInstitution("Test Primary", InstitutionCategory.PRIMARY_SCHOOL, "Nairobi", LocalDate.now());
        Institution jun = InstitutionFactory.createNewInstitution("Test Junior", InstitutionCategory.JUNIOR_SCHOOL, "Mombasa", LocalDate.now());
        Institution sen = InstitutionFactory.createNewInstitution("Test Senior", InstitutionCategory.SENIOR_SCHOOL, "Kisumu", LocalDate.now());
        Institution col = InstitutionFactory.createNewInstitution("Test College", InstitutionCategory.COLLEGE, "Nakuru", LocalDate.now());

        assertTrue(pri instanceof PrimarySchool);
        assertTrue(jun instanceof JuniorSchool);
        assertTrue(sen instanceof SeniorSchool);
        assertTrue(col instanceof College);

        assertEquals(InstitutionCategory.PRIMARY_SCHOOL, pri.getCategory());
        assertEquals(InstitutionCategory.JUNIOR_SCHOOL, jun.getCategory());
        assertEquals(InstitutionCategory.SENIOR_SCHOOL, sen.getCategory());
        assertEquals(InstitutionCategory.COLLEGE, col.getCategory());

        // Test polymorphic method execution
        assertNotNull(pri.getRecommendedBandwidth());
        assertNotNull(jun.getRecommendedBandwidth());
        assertNotNull(sen.getRecommendedBandwidth());
        assertNotNull(col.getRecommendedBandwidth());
    }

    @Test
    @DisplayName("Test Registration Fee constant is KSh 8,500")
    public void testRegistrationFee() {
        assertEquals(8500.0, Institution.REGISTRATION_FEE);
    }
}

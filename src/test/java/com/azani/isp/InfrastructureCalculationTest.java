package com.azani.isp;

import com.azani.isp.model.Infrastructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InfrastructureCalculationTest {

    @Test
    @DisplayName("Test LAN nodes pricing tiers based on Table 2")
    public void testLanPricingTiers() {
        // Table 2:
        // 2 - 10: 10,000
        // 11 - 20: 20,000
        // 21 - 40: 30,000
        // 41 - 100: 40,000
        assertEquals(0.0, Infrastructure.calculateLanCost(0));
        assertEquals(10000.0, Infrastructure.calculateLanCost(2));
        assertEquals(10000.0, Infrastructure.calculateLanCost(10));
        assertEquals(20000.0, Infrastructure.calculateLanCost(11));
        assertEquals(20000.0, Infrastructure.calculateLanCost(20));
        assertEquals(30000.0, Infrastructure.calculateLanCost(21));
        assertEquals(30000.0, Infrastructure.calculateLanCost(40));
        assertEquals(40000.0, Infrastructure.calculateLanCost(41));
        assertEquals(40000.0, Infrastructure.calculateLanCost(100));
    }

    @Test
    @DisplayName("Test ready institution pays only base installation fee of KSh 10,000")
    public void testReadyInstitutionInstallationCost() {
        Infrastructure infra = new Infrastructure(1, 100, true, 0, 0);
        assertEquals(10000.0, infra.getBaseInstallationFee());
        assertEquals(0.0, infra.getPcCost());
        assertEquals(0.0, infra.getLanCost());
        assertEquals(10000.0, infra.getTotalInstallationCost());
    }

    @Test
    @DisplayName("Test not-ready institution calculates PCs (40,000 each) and LAN node costs")
    public void testNonReadyInstitutionInstallationCost() {
        // 5 PCs @ 40,000 = 200,000; 8 LAN nodes in [2-10] = 10,000; Base = 10,000
        // Total = 220,000
        Infrastructure infra = new Infrastructure(2, 250, false, 5, 8);
        assertEquals(200000.0, infra.getPcCost());
        assertEquals(10000.0, infra.getLanCost());
        assertEquals(10000.0, infra.getBaseInstallationFee());
        assertEquals(220000.0, infra.getTotalInstallationCost());
    }
}

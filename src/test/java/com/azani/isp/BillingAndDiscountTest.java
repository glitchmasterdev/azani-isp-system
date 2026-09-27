package com.azani.isp;

import com.azani.isp.model.BandwidthPackage;
import com.azani.isp.model.Bill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BillingAndDiscountTest {

    @Test
    @DisplayName("Test Bandwidth standard prices from Table 1")
    public void testTable1Prices() {
        assertEquals(1200.0, BandwidthPackage.MBPS_4.getMonthlyCost());
        assertEquals(2000.0, BandwidthPackage.MBPS_10.getMonthlyCost());
        assertEquals(3500.0, BandwidthPackage.MBPS_20.getMonthlyCost());
        assertEquals(4000.0, BandwidthPackage.MBPS_25.getMonthlyCost());
        assertEquals(7000.0, BandwidthPackage.MBPS_50.getMonthlyCost());
    }

    @Test
    @DisplayName("Test 10% Upgrade discount rule")
    public void testUpgradeDiscountRule() {
        // Upgrade to 50 MBPS (7,000) -> 10% discount = 6,300
        assertEquals(6300.0, BandwidthPackage.MBPS_50.calculateMonthlyFee(true), 0.001);

        // Upgrade to 25 MBPS (4,000) -> 10% discount = 3,600
        assertEquals(3600.0, BandwidthPackage.MBPS_25.calculateMonthlyFee(true), 0.001);

        // Upgrade to 20 MBPS (3,500) -> 10% discount = 3,150
        assertEquals(3150.0, BandwidthPackage.MBPS_20.calculateMonthlyFee(true), 0.001);

        // Standard without upgrade
        assertEquals(7000.0, BandwidthPackage.MBPS_50.calculateMonthlyFee(false), 0.001);
    }

    @Test
    @DisplayName("Test 15% Overdue Fine surcharge after month end")
    public void testOverdueFineSurcharge() {
        // August 2026 bill, base charge 2,000
        Bill bill = new Bill(1, 8, 2026, 2000.0);
        assertEquals(LocalDate.of(2026, 8, 31), bill.getDueDate());
        assertEquals(LocalDate.of(2026, 9, 10), bill.getDisconnectionDate());

        // Evaluated on Sept 2 (past month end, before 10th) -> OVERDUE + 15% fine (300)
        bill.evaluateStatus(LocalDate.of(2026, 9, 2));
        assertEquals("OVERDUE", bill.getStatus());
        assertEquals(300.0, bill.getOverdueFine(), 0.001);
        assertEquals(2300.0, bill.getTotalDue(), 0.001);
    }

    @Test
    @DisplayName("Test Disconnection past 10th of subsequent month and KSh 1,000 Reconnection fee")
    public void testDisconnectionAndReconnection() {
        // August 2026 bill, base charge 2,000
        Bill bill = new Bill(1, 8, 2026, 2000.0);

        // Evaluated on Sept 11 (past 10th) -> DISCONNECTED
        bill.evaluateStatus(LocalDate.of(2026, 9, 11));
        assertEquals("DISCONNECTED", bill.getStatus());
        assertEquals(300.0, bill.getOverdueFine(), 0.001);

        // Reconnection surcharge applied
        bill.applyReconnectionFee();
        assertEquals(1000.0, bill.getReconnectionFee());
        assertEquals(3300.0, bill.getTotalDue(), 0.001); // 2000 + 300 + 1000
    }
}

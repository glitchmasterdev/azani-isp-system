package com.azani.isp;

import com.azani.isp.dao.DatabaseConnection;
import com.azani.isp.service.ReportService;
import com.azani.isp.ui.CLIApp;
import com.azani.isp.ui.MainWindow;

import javax.swing.*;
import java.awt.*;

/**
 * Azani Internet Service Provider Information System
 * SCO200 Object Oriented Programming II Project
 * September - November 2026
 *
 * Main entry point for the application.
 */
public class Main {

    public static void main(String[] args) {
        boolean cliMode = false;
        boolean testMode = false;

        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg)) {
                cliMode = true;
            } else if ("--cli-test".equalsIgnoreCase(arg)) {
                testMode = true;
            }
        }

        // Initialize Database schema
        DatabaseConnection.initializeDatabase();

        if (testMode) {
            runAutomatedDemonstration();
            return;
        }

        if (cliMode || GraphicsEnvironment.isHeadless()) {
            new CLIApp().start();
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            SwingUtilities.invokeLater(() -> {
                MainWindow window = new MainWindow();
                window.setVisible(true);
            });
        }
    }

    private static void runAutomatedDemonstration() {
        System.out.println("==========================================================================");
        System.out.println(" AZANI INTERNET SERVICE PROVIDER INFORMATION SYSTEM - DEMO & EVALUATION");
        System.out.println("==========================================================================");
        System.out.println("1. Seeding sample institutional data...");
        DatabaseConnection.seedSampleData();

        ReportService reportService = new ReportService();
        try {
            System.out.println("\n--- TASK 3(a): REGISTERED INSTITUTIONS ---");
            System.out.println(reportService.generateRegisteredInstitutionsReport());

            System.out.println("\n--- TASK 3(b): LIST OF DEFAULTERS (15% FINE) ---");
            System.out.println(reportService.generateDefaultersReport());

            System.out.println("\n--- TASK 3(c): DISCONNECTION ISSUES ---");
            System.out.println(reportService.generateDisconnectionReport());

            System.out.println("\n--- TASK 3(d): INFRASTRUCTURAL REQUIREMENTS ---");
            System.out.println(reportService.generateInfrastructureReport());

            System.out.println("\n--- TASK 4(a-e) & TASK 5: MASTER COMPUTATIONS & FINANCIAL BREAKDOWN ---");
            System.out.println(reportService.generateMasterComputationReport());

            System.out.println("Demonstration run completed successfully.");
        } catch (Exception e) {
            System.err.println("Evaluation error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

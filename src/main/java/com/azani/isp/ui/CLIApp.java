package com.azani.isp.ui;

import com.azani.isp.dao.DatabaseConnection;
import com.azani.isp.model.*;
import com.azani.isp.service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Command Line Interface for Azani ISP Information System.
 */
public class CLIApp {
    private final InstitutionService institutionService;
    private final InfrastructureService infrastructureService;
    private final BillingService billingService;
    private final PaymentService paymentService;
    private final ComputationService computationService;
    private final ReportService reportService;
    private final Scanner scanner;

    public CLIApp() {
        this.institutionService = new InstitutionService();
        this.infrastructureService = new InfrastructureService();
        this.billingService = new BillingService();
        this.paymentService = new PaymentService();
        this.computationService = new ComputationService();
        this.reportService = new ReportService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("====================================================================");
        System.out.println("   AZANI INTERNET SERVICE PROVIDER INFORMATION SYSTEM (SCO200)");
        System.out.println("   Connected Database: " + DatabaseConnection.getActiveDbType().toUpperCase());
        System.out.println("====================================================================");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Select an option (0-10): ");
            String input = scanner.nextLine().trim();

            try {
                switch (input) {
                    case "1":
                        handleRegisterInstitution();
                        break;
                    case "2":
                        handleCapturePayment();
                        break;
                    case "3":
                        System.out.println(reportService.generateRegisteredInstitutionsReport());
                        break;
                    case "4":
                        System.out.println(reportService.generateDefaultersReport());
                        break;
                    case "5":
                        System.out.println(reportService.generateDisconnectionReport());
                        break;
                    case "6":
                        System.out.println(reportService.generateInfrastructureReport());
                        break;
                    case "7":
                        handleAssessInfrastructure();
                        break;
                    case "8":
                        handleSubscriptionAndUpgrade();
                        break;
                    case "9":
                        System.out.println(reportService.generateMasterComputationReport());
                        break;
                    case "10":
                        DatabaseConnection.seedSampleData();
                        System.out.println("Sample institutional records loaded into database.");
                        break;
                    case "0":
                        running = false;
                        System.out.println("Thank you for using Azani ISP Information System.");
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a number between 0 and 10.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n----------------- MAIN OPERATIONS MENU -----------------");
        System.out.println(" 1. Register Institution & Contact Person (Req 1)");
        System.out.println(" 2. Capture Payments [Registration, Installation, Monthly, Reconnection] (Req 2)");
        System.out.println(" 3. View Registered Institutions (Req 3a)");
        System.out.println(" 4. View List of Defaulters [15% fine] (Req 3b)");
        System.out.println(" 5. View Disconnection Issues Report (Req 3c)");
        System.out.println(" 6. View Infrastructural Requirements Report (Req 3d)");
        System.out.println(" 7. Assess / Record Infrastructure & Hardware (PCs & LAN)");
        System.out.println(" 8. Manage Bandwidth Subscriptions & 10% Upgrades");
        System.out.println(" 9. Perform All Computations & Generate Master Report (Req 4a-e, 5)");
        System.out.println("10. Seed Demonstration Data");
        System.out.println(" 0. Exit System");
        System.out.println("---------------------------------------------------------");
    }

    private void handleRegisterInstitution() throws Exception {
        System.out.println("\n--- Register New Learning Institution ---");
        System.out.print("Institution Name: ");
        String name = scanner.nextLine().trim();

        System.out.println("Select Category:");
        System.out.println("1. Primary School");
        System.out.println("2. Junior School");
        System.out.println("3. Senior School");
        System.out.println("4. College");
        System.out.print("Choice (1-4): ");
        int catChoice = Integer.parseInt(scanner.nextLine().trim());
        InstitutionCategory category;
        switch (catChoice) {
            case 1: category = InstitutionCategory.PRIMARY_SCHOOL; break;
            case 2: category = InstitutionCategory.JUNIOR_SCHOOL; break;
            case 3: category = InstitutionCategory.SENIOR_SCHOOL; break;
            case 4: category = InstitutionCategory.COLLEGE; break;
            default: throw new IllegalArgumentException("Invalid category choice.");
        }

        System.out.print("Physical Address: ");
        String address = scanner.nextLine().trim();

        System.out.println("\n--- Contact Person Details ---");
        System.out.print("Full Name: ");
        String contactName = scanner.nextLine().trim();
        System.out.print("Designation / Role: ");
        String designation = scanner.nextLine().trim();
        System.out.print("Phone Number: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Email Address: ");
        String email = scanner.nextLine().trim();
        System.out.print("National ID / Staff ID: ");
        String nationalId = scanner.nextLine().trim();

        Institution inst = institutionService.registerInstitution(name, category, address, LocalDate.now(),
                contactName, designation, phone, email, nationalId);
        System.out.println("Successfully registered: " + inst);
        System.out.println("Note: Mandatory registration fee of KSh 8,500 is now payable.");
    }

    private void handleCapturePayment() throws Exception {
        System.out.println("\n--- Capture Payment ---");
        System.out.println("1. Registration Fee (KSh 8,500)");
        System.out.println("2. Installation Fee (Base + Hardware)");
        System.out.println("3. Monthly Internet Bill Payment");
        System.out.println("4. Reconnection Surcharge Fee (KSh 1,000)");
        System.out.print("Select Payment Type (1-4): ");
        String choice = scanner.nextLine().trim();

        System.out.print("Enter Institution ID: ");
        int instId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Transaction Reference No (e.g. MPESA/Bank Ref, press Enter for auto): ");
        String refNo = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                Payment regPay = paymentService.captureRegistrationFee(instId, refNo, "Registration Fee Payment");
                System.out.println("Registration payment recorded: " + regPay);
                break;
            case "2":
                Infrastructure infra = infrastructureService.getInfrastructure(instId);
                double amount = infra != null ? infra.getTotalInstallationCost() : Institution.BASE_INSTALLATION_FEE;
                System.out.println("Computed installation cost: KSh " + String.format("%,.2f", amount));
                System.out.print("Confirm amount to pay (press Enter for KSh " + amount + "): ");
                String amtStr = scanner.nextLine().trim();
                if (!amtStr.isEmpty()) amount = Double.parseDouble(amtStr);
                Payment instPay = paymentService.captureInstallationFee(instId, amount, refNo, "Installation fee payment");
                System.out.println("Installation payment recorded: " + instPay);
                break;
            case "3":
                List<Bill> bills = billingService.getBillsForInstitution(instId);
                if (bills.isEmpty()) {
                    System.out.println("No open bills found for institution " + instId + ".");
                    return;
                }
                System.out.println("Select bill to pay:");
                for (Bill b : bills) {
                    System.out.println(" - Bill #" + b.getId() + " | Month " + b.getBillingMonth() + "/" + b.getBillingYear() +
                            " | Total Due: KSh " + String.format("%,.2f", b.getTotalDue()) + " | Status: " + b.getStatus());
                }
                System.out.print("Enter Bill ID: ");
                int billId = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Enter Amount Paid: ");
                double payAmt = Double.parseDouble(scanner.nextLine().trim());
                Payment mthPay = paymentService.captureMonthlyPayment(billId, payAmt, refNo, "Monthly payment");
                System.out.println("Monthly payment recorded: " + mthPay);
                break;
            case "4":
                Payment recPay = paymentService.captureReconnectionFee(instId, refNo, "Reconnection surcharge fee");
                System.out.println("Reconnection payment recorded: " + recPay);
                break;
            default:
                System.out.println("Invalid payment type choice.");
        }
    }

    private void handleAssessInfrastructure() throws Exception {
        System.out.println("\n--- Assess Infrastructure Requirements ---");
        System.out.print("Institution ID: ");
        int instId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Number of Users: ");
        int users = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Is the institution site already ready for connectivity? (y/n): ");
        boolean ready = scanner.nextLine().trim().equalsIgnoreCase("y");

        int pcs = 0;
        int nodes = 0;
        if (!ready) {
            System.out.print("Number of Personal Computers to purchase from Azani (KSh 40,000 each): ");
            pcs = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Number of LAN nodes required: ");
            nodes = Integer.parseInt(scanner.nextLine().trim());
        }

        Infrastructure infra = infrastructureService.assessInfrastructure(instId, users, ready, pcs, nodes);
        System.out.println("Assessment Saved:\n" + infra);
    }

    private void handleSubscriptionAndUpgrade() throws Exception {
        System.out.println("\n--- Bandwidth Subscription & Upgrades ---");
        System.out.print("Institution ID: ");
        int instId = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("Available Packages (Table 1):");
        System.out.println("1. 4 MBPS  - KSh 1,200/mo");
        System.out.println("2. 10 MBPS - KSh 2,000/mo");
        System.out.println("3. 20 MBPS - KSh 3,500/mo");
        System.out.println("4. 25 MBPS - KSh 4,000/mo");
        System.out.println("5. 50 MBPS - KSh 7,000/mo");
        System.out.print("Choose package (1-5): ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        BandwidthPackage pkg;
        switch (choice) {
            case 1: pkg = BandwidthPackage.MBPS_4; break;
            case 2: pkg = BandwidthPackage.MBPS_10; break;
            case 3: pkg = BandwidthPackage.MBPS_20; break;
            case 4: pkg = BandwidthPackage.MBPS_25; break;
            case 5: pkg = BandwidthPackage.MBPS_50; break;
            default: throw new IllegalArgumentException("Invalid package option.");
        }

        System.out.print("Is this an UPGRADE to higher bandwidth? (y/n): ");
        boolean isUpgrade = scanner.nextLine().trim().equalsIgnoreCase("y");

        Subscription sub;
        if (isUpgrade) {
            sub = billingService.upgradeSubscription(instId, pkg);
            System.out.println("Upgrade applied with 10% discount: " + sub);
        } else {
            sub = billingService.subscribe(instId, pkg, LocalDate.now());
            System.out.println("Standard subscription created: " + sub);
        }
    }
}

package com.azani.isp.service;

import com.azani.isp.model.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Service for generating comprehensive formatted reports as specified in:
 * Task 3: Lists (Registered institutions, Defaulters, Disconnections, Infrastructure requirements)
 * Task 4: Computations (Installation, Assorted hardware, Upgraded services, Category revenue, Aggregates)
 * Task 5: Executive Reports and Invoices
 */
public class ReportService {
    private final InstitutionService institutionService;
    private final InfrastructureService infrastructureService;
    private final BillingService billingService;
    private final ComputationService computationService;

    public ReportService() {
        this.institutionService = new InstitutionService();
        this.infrastructureService = new InfrastructureService();
        this.billingService = new BillingService();
        this.computationService = new ComputationService();
    }

    public ReportService(InstitutionService instService, InfrastructureService infraService,
                         BillingService billService, ComputationService compService) {
        this.institutionService = instService;
        this.infrastructureService = infraService;
        this.billingService = billService;
        this.computationService = compService;
    }

    /**
     * Task 3(a): Generate Report of Registered Institutions with Contact Person Details.
     */
    public String generateRegisteredInstitutionsReport() throws SQLException {
        List<Institution> list = institutionService.getAllInstitutions();
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================================================\n");
        sb.append("                                   AZANI INTERNET SERVICE PROVIDER - REGISTERED INSTITUTIONS\n");
        sb.append("========================================================================================================================\n");
        sb.append(String.format("%-4s | %-30s | %-16s | %-20s | %-15s | %-15s | %-12s\n",
                "ID", "Institution Name", "Category", "Contact Person", "Phone", "Reg Date", "Status"));
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");

        for (Institution inst : list) {
            ContactPerson cp = inst.getContactPerson();
            String contactName = cp != null ? cp.getFullName() : "N/A";
            String contactPhone = cp != null ? cp.getPhone() : "N/A";

            sb.append(String.format("%-4d | %-30s | %-16s | %-20s | %-15s | %-15s | %-12s\n",
                    inst.getId(),
                    truncate(inst.getName(), 30),
                    inst.getCategory().getDisplayName(),
                    truncate(contactName, 20),
                    truncate(contactPhone, 15),
                    inst.getRegistrationDate(),
                    inst.getStatus()));
        }
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");
        sb.append("Total Registered Institutions: ").append(list.size()).append("\n\n");
        return sb.toString();
    }

    /**
     * Task 3(b): Generate Report of Defaulters (institutions failing to pay by end of current month, 15% fine).
     */
    public String generateDefaultersReport() throws SQLException {
        List<Bill> defaulters = billingService.getDefaulterBills();
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================================================\n");
        sb.append("                                    AZANI INTERNET SERVICE PROVIDER - LIST OF DEFAULTERS\n");
        sb.append("                                (Institutions Surcharged 15% Overdue Fine Past Month End)\n");
        sb.append("========================================================================================================================\n");
        sb.append(String.format("%-4s | %-28s | %-10s | %-12s | %-14s | %-12s | %-12s | %-10s\n",
                "ID", "Institution Name", "Period", "Base Bill", "Overdue Fine", "Total Due", "Due Date", "Status"));
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");

        double totalDefaulterAmount = 0.0;
        double totalFineAmount = 0.0;

        for (Bill bill : defaulters) {
            Institution inst = institutionService.getInstitutionById(bill.getInstitutionId());
            String instName = inst != null ? inst.getName() : "ID #" + bill.getInstitutionId();
            sb.append(String.format("%-4d | %-28s | %02d/%-7d | KSh %-8s | KSh %-10s | KSh %-8s | %-12s | %-10s\n",
                    bill.getId(),
                    truncate(instName, 28),
                    bill.getBillingMonth(), bill.getBillingYear(),
                    formatMoney(bill.getBaseCharge()),
                    formatMoney(bill.getOverdueFine()),
                    formatMoney(bill.getTotalDue()),
                    bill.getDueDate(),
                    bill.getStatus()));

            totalDefaulterAmount += bill.getTotalDue();
            totalFineAmount += bill.getOverdueFine();
        }
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");
        sb.append("Total Defaulters Count: ").append(defaulters.size())
          .append(" | Total Overdue Fines: KSh ").append(formatMoney(totalFineAmount))
          .append(" | Total Outstanding: KSh ").append(formatMoney(totalDefaulterAmount)).append("\n\n");
        return sb.toString();
    }

    /**
     * Task 3(c): Generate Report of Institutions with Disconnection Issues.
     * (Unpaid after the 10th day of the subsequent month).
     */
    public String generateDisconnectionReport() throws SQLException {
        List<Bill> disconnected = billingService.getDisconnectedBills();
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================================================\n");
        sb.append("                             AZANI INTERNET SERVICE PROVIDER - DISCONNECTION ISSUES REPORT\n");
        sb.append("                        (Services Disconnected Due to Non-payment Past 10th of Subsequent Month)\n");
        sb.append("========================================================================================================================\n");
        sb.append(String.format("%-4s | %-28s | %-16s | %-15s | %-12s | %-12s | %-16s\n",
                "ID", "Institution Name", "Category", "Contact Phone", "Disconn Date", "Total Due", "Reconnection Fee"));
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");

        for (Bill bill : disconnected) {
            Institution inst = institutionService.getInstitutionById(bill.getInstitutionId());
            String name = inst != null ? inst.getName() : "Unknown";
            String cat = inst != null ? inst.getCategory().getDisplayName() : "Unknown";
            String phone = (inst != null && inst.getContactPerson() != null) ? inst.getContactPerson().getPhone() : "N/A";

            sb.append(String.format("%-4d | %-28s | %-16s | %-15s | %-12s | KSh %-8s | KSh 1,000.00\n",
                    bill.getId(),
                    truncate(name, 28),
                    truncate(cat, 16),
                    truncate(phone, 15),
                    bill.getDisconnectionDate(),
                    formatMoney(bill.getTotalDue())));
        }
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");
        sb.append("Notice: A re-connection surcharge fee of KSh 1,000 applies upon bill settlement.\n\n");
        return sb.toString();
    }

    /**
     * Task 3(d): Details of Infrastructural Requirements for each Institution.
     */
    public String generateInfrastructureReport() throws SQLException {
        List<Institution> list = institutionService.getAllInstitutions();
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================================================\n");
        sb.append("                           AZANI INTERNET SERVICE PROVIDER - INFRASTRUCTURAL REQUIREMENTS REPORT\n");
        sb.append("========================================================================================================================\n");
        sb.append(String.format("%-4s | %-28s | %-6s | %-7s | %-8s | %-12s | %-6s | %-12s | %-14s\n",
                "ID", "Institution Name", "Users", "Ready?", "PCs Req", "PC Cost", "Nodes", "LAN Cost", "Total Install"));
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");

        for (Institution inst : list) {
            Infrastructure infra = inst.getInfrastructure();
            if (infra == null) {
                infra = infrastructureService.getInfrastructure(inst.getId());
            }

            int users = infra != null ? infra.getNumUsers() : 0;
            String readyStr = (infra != null && infra.isReady()) ? "YES" : "NO";
            int pcs = infra != null ? infra.getPcsPurchased() : 0;
            double pcCost = infra != null ? infra.getPcCost() : 0.0;
            int nodes = infra != null ? infra.getLanNodes() : 0;
            double lanCost = infra != null ? infra.getLanCost() : 0.0;
            double totalInstall = infra != null ? infra.getTotalInstallationCost() : Institution.BASE_INSTALLATION_FEE;

            sb.append(String.format("%-4d | %-28s | %-6d | %-7s | %-8d | KSh %-8s | %-6d | KSh %-8s | KSh %-10s\n",
                    inst.getId(),
                    truncate(inst.getName(), 28),
                    users,
                    readyStr,
                    pcs,
                    formatMoney(pcCost),
                    nodes,
                    formatMoney(lanCost),
                    formatMoney(totalInstall)));
        }
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");
        sb.append("Notes: Personal Computers = KSh 40,000 each | LAN node pricing per Table 2 | Base Installation = KSh 10,000\n\n");
        return sb.toString();
    }

    /**
     * Task 4(a)-(e) & 5: Master Comprehensive Financial & Computation Report.
     */
    public String generateMasterComputationReport() throws SQLException {
        StringBuilder sb = new StringBuilder();

        // 4(a) Total installation cost
        sb.append("========================================================================================================================\n");
        sb.append("                    COMPUTATION 4(a): TOTAL INSTALLATION COST FOR EACH INSTITUTION\n");
        sb.append("========================================================================================================================\n");
        Map<Institution, Double> installCosts = computationService.computeTotalInstallationCosts();
        double grandInstall = 0.0;
        for (Map.Entry<Institution, Double> entry : installCosts.entrySet()) {
            sb.append(String.format(" - %-35s [%-15s] : KSh %s\n",
                    entry.getKey().getName(), entry.getKey().getCategory().getDisplayName(), formatMoney(entry.getValue())));
            grandInstall += entry.getValue();
        }
        sb.append("   Grand Total Installation Costs: KSh ").append(formatMoney(grandInstall)).append("\n\n");

        // 4(b) Assorted hardware costs
        sb.append("========================================================================================================================\n");
        sb.append("          COMPUTATION 4(b): COST OF PERSONAL COMPUTERS AND LAN SERVICES (ASSORTED SERVICES)\n");
        sb.append("========================================================================================================================\n");
        Map<Institution, Infrastructure> assorted = computationService.computeAssortedHardwareCosts();
        double grandAssorted = 0.0;
        for (Map.Entry<Institution, Infrastructure> entry : assorted.entrySet()) {
            Infrastructure inf = entry.getValue();
            double totalHardware = inf.getHardwareCost();
            grandAssorted += totalHardware;
            sb.append(String.format(" - %-30s | PCs: %2d (KSh %-10s) | LAN Nodes: %2d (KSh %-9s) | Subtotal: KSh %s\n",
                    entry.getKey().getName(),
                    inf.getPcsPurchased(), formatMoney(inf.getPcCost()),
                    inf.getLanNodes(), formatMoney(inf.getLanCost()),
                    formatMoney(totalHardware)));
        }
        sb.append("   Total Assorted Hardware & LAN Revenue: KSh ").append(formatMoney(grandAssorted)).append("\n\n");

        // 4(c) Upgraded internet services
        sb.append("========================================================================================================================\n");
        sb.append("              COMPUTATION 4(c): TOTAL MONTHLY CHARGES FOR UPGRADED INTERNET SERVICES\n");
        sb.append("                           (Includes 10% Discount on Upgraded Bandwidth)\n");
        sb.append("========================================================================================================================\n");
        List<Map<String, Object>> upgraded = computationService.getUpgradedSubscriptionsDetails();
        double totalUpgradedCharges = 0.0;
        for (Map<String, Object> map : upgraded) {
            Institution inst = (Institution) map.get("institution");
            BandwidthPackage pkg = (BandwidthPackage) map.get("package");
            double baseCost = (Double) map.get("baseCost");
            double finalCost = (Double) map.get("finalCost");
            totalUpgradedCharges += finalCost;

            sb.append(String.format(" - %-30s | Package: %-8s | Standard: KSh %-8s | Discount: 10%% | Final: KSh %s/mo\n",
                    inst.getName(), pkg.getLabel(), formatMoney(baseCost), formatMoney(finalCost)));
        }
        sb.append("   Total Monthly Revenue from Upgraded Services: KSh ").append(formatMoney(totalUpgradedCharges)).append("\n\n");

        // 4(d) Category Breakdown
        sb.append("========================================================================================================================\n");
        sb.append(" COMPUTATION 4(d): CHARGES, OVERDUE FINES (15%) & RECONNECTION FEES (KSh 1,000) PER CATEGORY\n");
        sb.append("========================================================================================================================\n");
        sb.append(String.format("%-18s | %-8s | %-16s | %-16s | %-16s | %-16s\n",
                "Category", "Count", "Monthly Charges", "Overdue Fines", "Reconnect Fees", "Category Total"));
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");
        Map<InstitutionCategory, CategoryFinancialSummary> categoryMap = computationService.computeCategoryRevenueBreakdown();
        double allMonthly = 0, allFines = 0, allReconnect = 0, allGrand = 0;
        for (CategoryFinancialSummary s : categoryMap.values()) {
            sb.append(String.format("%-18s | %-8d | KSh %-12s | KSh %-12s | KSh %-12s | KSh %-12s\n",
                    s.getCategory().getDisplayName(),
                    s.getInstitutionCount(),
                    formatMoney(s.getTotalMonthlyCharges()),
                    formatMoney(s.getTotalOverdueFines()),
                    formatMoney(s.getTotalReconnectionFees()),
                    formatMoney(s.getGrandTotal())));
            allMonthly += s.getTotalMonthlyCharges();
            allFines += s.getTotalOverdueFines();
            allReconnect += s.getTotalReconnectionFees();
            allGrand += s.getGrandTotal();
        }
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-18s | %-8s | KSh %-12s | KSh %-12s | KSh %-12s | KSh %-12s\n\n",
                "TOTALS", "-", formatMoney(allMonthly), formatMoney(allFines), formatMoney(allReconnect), formatMoney(allGrand)));

        // 4(e) Aggregates by institution
        sb.append("========================================================================================================================\n");
        sb.append("           COMPUTATION 4(e): AGGREGATE AMOUNT FOR EACH SERVICE SORTED BY INSTITUTION\n");
        sb.append("========================================================================================================================\n");
        sb.append(String.format("%-25s | %-10s | %-10s | %-10s | %-10s | %-10s | %-10s | %-10s | %-12s\n",
                "Institution", "Category", "Reg Fee", "Install", "PCs Cost", "LAN Cost", "Monthly", "Fines+Rec", "Grand Total"));
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");
        List<ServiceAggregateSummary> aggregates = computationService.computeServiceAggregatesByInstitution();
        double totalSystemAmount = 0.0;
        for (ServiceAggregateSummary agg : aggregates) {
            double penaltyTotal = agg.getOverdueFines() + agg.getReconnectionFees();
            sb.append(String.format("%-25s | %-10s | %-10s | %-10s | %-10s | %-10s | %-10s | %-10s | KSh %-10s\n",
                    truncate(agg.getInstitutionName(), 25),
                    truncate(agg.getCategory().name(), 10),
                    formatMoney(agg.getRegistrationFee()),
                    formatMoney(agg.getInstallationBaseFee()),
                    formatMoney(agg.getPcEquipmentCost()),
                    formatMoney(agg.getLanNodesCost()),
                    formatMoney(agg.getMonthlyInternetCharges()),
                    formatMoney(penaltyTotal),
                    formatMoney(agg.getTotalAggregateAmount())));
            totalSystemAmount += agg.getTotalAggregateAmount();
        }
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");
        sb.append("Overall System-Wide Aggregate Revenue: KSh ").append(formatMoney(totalSystemAmount)).append("\n\n");

        return sb.toString();
    }

    private static String formatMoney(double amount) {
        return String.format("%,.2f", amount);
    }

    private static String truncate(String str, int maxLen) {
        if (str == null) return "";
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 3) + "...";
    }
}

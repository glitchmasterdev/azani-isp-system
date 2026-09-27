package com.azani.isp.service;

import com.azani.isp.dao.BillingDAO;
import com.azani.isp.dao.InfrastructureDAO;
import com.azani.isp.dao.InstitutionDAO;
import com.azani.isp.dao.PaymentDAO;
import com.azani.isp.model.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Implements the core mathematical, business, and financial computations required by SCO200:
 * 4(a) Total installation cost for each institution
 * 4(b) Cost of personal computers and LAN services for institutions with assorted services
 * 4(c) Total monthly charges for upgraded internet services
 * 4(d) Total monthly charges, overdue fines, and reconnection fees per category of institution
 * 4(e) Aggregate amount for each service sorted by an institution
 */
public class ComputationService {
    private final InstitutionDAO institutionDAO;
    private final InfrastructureDAO infrastructureDAO;
    private final BillingDAO billingDAO;
    private final PaymentDAO paymentDAO;

    public ComputationService() {
        this.institutionDAO = new InstitutionDAO();
        this.infrastructureDAO = new InfrastructureDAO();
        this.billingDAO = new BillingDAO();
        this.paymentDAO = new PaymentDAO();
    }

    public ComputationService(InstitutionDAO instDao, InfrastructureDAO infraDao, BillingDAO billDao, PaymentDAO payDao) {
        this.institutionDAO = instDao;
        this.infrastructureDAO = infraDao;
        this.billingDAO = billDao;
        this.paymentDAO = payDao;
    }

    /**
     * Requirement 4(a): Total installation cost for each institution.
     * Computes base installation fee (10,000) + PC cost + LAN node tier cost.
     */
    public Map<Institution, Double> computeTotalInstallationCosts() throws SQLException {
        Map<Institution, Double> results = new LinkedHashMap<>();
        List<Institution> institutions = institutionDAO.findAll();
        for (Institution inst : institutions) {
            Infrastructure infra = infrastructureDAO.findByInstitutionId(inst.getId());
            double total = (infra != null) ? infra.getTotalInstallationCost() : Institution.BASE_INSTALLATION_FEE;
            results.put(inst, total);
        }
        return results;
    }

    /**
     * Requirement 4(b): Cost of personal computers and LAN services for institutions with assorted services.
     * Filters for institutions that purchased computers and/or LAN nodes from Azani.
     */
    public Map<Institution, Infrastructure> computeAssortedHardwareCosts() throws SQLException {
        Map<Institution, Infrastructure> results = new LinkedHashMap<>();
        List<Institution> institutions = institutionDAO.findAll();
        for (Institution inst : institutions) {
            Infrastructure infra = infrastructureDAO.findByInstitutionId(inst.getId());
            if (infra != null && (infra.getPcsPurchased() > 0 || infra.getLanNodes() > 0)) {
                results.put(inst, infra);
            }
        }
        return results;
    }

    /**
     * Requirement 4(c): Total monthly charges for upgraded internet services.
     * Computes the sum of monthly charges for institutions with upgraded bandwidth (with 10% discount).
     */
    public double computeUpgradedServiceCharges() throws SQLException {
        double total = 0.0;
        List<Subscription> subscriptions = billingDAO.findAllSubscriptions();
        for (Subscription sub : subscriptions) {
            if (sub.isUpgraded() && sub.isActive()) {
                total += sub.getFinalMonthlyCost();
            }
        }
        return total;
    }

    /**
     * Requirement 4(c) helper: Returns list of all upgraded subscriptions.
     */
    public List<Map<String, Object>> getUpgradedSubscriptionsDetails() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Subscription> subscriptions = billingDAO.findAllSubscriptions();
        for (Subscription sub : subscriptions) {
            if (sub.isUpgraded() && sub.isActive()) {
                Institution inst = institutionDAO.findById(sub.getInstitutionId());
                Map<String, Object> map = new HashMap<>();
                map.put("institution", inst);
                map.put("package", sub.getBandwidthPackage());
                map.put("baseCost", sub.getBaseMonthlyCost());
                map.put("discountPercent", sub.getDiscountPercent());
                map.put("finalCost", sub.getFinalMonthlyCost());
                list.add(map);
            }
        }
        return list;
    }

    /**
     * Requirement 4(d): Total monthly charges for internet services, overdue fines,
     * and re-connection fees generated from each category of institution.
     */
    public Map<InstitutionCategory, CategoryFinancialSummary> computeCategoryRevenueBreakdown() throws SQLException {
        Map<InstitutionCategory, CategoryFinancialSummary> summaryMap = new LinkedHashMap<>();
        for (InstitutionCategory cat : InstitutionCategory.values()) {
            summaryMap.put(cat, new CategoryFinancialSummary(cat));
        }

        List<Bill> bills = billingDAO.findAllBills();
        for (Bill bill : bills) {
            Institution inst = institutionDAO.findById(bill.getInstitutionId());
            if (inst != null) {
                CategoryFinancialSummary catSummary = summaryMap.get(inst.getCategory());
                if (catSummary != null) {
                    catSummary.addRecord(bill.getBaseCharge(), bill.getOverdueFine(), bill.getReconnectionFee());
                }
            }
        }
        return summaryMap;
    }

    /**
     * Requirement 4(e): Aggregate amount for each service sorted by an institution.
     * Aggregates Registration, Installation Base, PC hardware, LAN nodes, Monthly charges,
     * Overdue fines, and Reconnection fees per institution, sorted alphabetically by institution name.
     */
    public List<ServiceAggregateSummary> computeServiceAggregatesByInstitution() throws SQLException {
        List<Institution> institutions = institutionDAO.findAll();
        institutions.sort(Comparator.comparing(Institution::getName));

        List<ServiceAggregateSummary> aggregates = new ArrayList<>();

        for (Institution inst : institutions) {
            ServiceAggregateSummary agg = new ServiceAggregateSummary(inst.getId(), inst.getName(), inst.getCategory());

            // 1. Registration Fee: KSh 8,500
            agg.setRegistrationFee(Institution.REGISTRATION_FEE);

            // 2 & 3. Infrastructure & Assorted Equipment (PCs & LAN nodes)
            Infrastructure infra = infrastructureDAO.findByInstitutionId(inst.getId());
            if (infra != null) {
                agg.setInstallationBaseFee(infra.getBaseInstallationFee());
                agg.setPcEquipmentCost(infra.getPcCost());
                agg.setLanNodesCost(infra.getLanCost());
            } else {
                agg.setInstallationBaseFee(Institution.BASE_INSTALLATION_FEE);
                agg.setPcEquipmentCost(0.0);
                agg.setLanNodesCost(0.0);
            }

            // 4, 5 & 6. Monthly charges, Overdue fines, Reconnection fees from Bills
            List<Bill> bills = billingDAO.findBillsByInstitutionId(inst.getId());
            double monthlyTotal = 0.0;
            double fineTotal = 0.0;
            double recTotal = 0.0;

            for (Bill b : bills) {
                monthlyTotal += b.getBaseCharge();
                fineTotal += b.getOverdueFine();
                recTotal += b.getReconnectionFee();
            }

            // If no bills yet, check active subscription for monthly charge
            if (bills.isEmpty()) {
                Subscription sub = billingDAO.findSubscriptionByInstitutionId(inst.getId());
                if (sub != null) {
                    monthlyTotal = sub.getFinalMonthlyCost();
                }
            }

            agg.setMonthlyInternetCharges(monthlyTotal);
            agg.setOverdueFines(fineTotal);
            agg.setReconnectionFees(recTotal);
            agg.recalculateTotal();

            aggregates.add(agg);
        }

        return aggregates;
    }
}

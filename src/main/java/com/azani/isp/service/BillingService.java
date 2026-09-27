package com.azani.isp.service;

import com.azani.isp.dao.BillingDAO;
import com.azani.isp.dao.InstitutionDAO;
import com.azani.isp.model.BandwidthPackage;
import com.azani.isp.model.Bill;
import com.azani.isp.model.Subscription;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service managing bandwidth subscriptions, upgrades (10% discount),
 * monthly bill generation, overdue fines (15%), disconnections, and reconnection fees.
 */
public class BillingService {
    private final BillingDAO billingDAO;
    private final InstitutionDAO institutionDAO;

    public BillingService() {
        this.billingDAO = new BillingDAO();
        this.institutionDAO = new InstitutionDAO();
    }

    public BillingService(BillingDAO billingDAO, InstitutionDAO institutionDAO) {
        this.billingDAO = billingDAO;
        this.institutionDAO = institutionDAO;
    }

    /**
     * Subscribes an institution to an initial bandwidth package.
     */
    public Subscription subscribe(int institutionId, BandwidthPackage pkg, LocalDate startDate) throws SQLException {
        Subscription sub = new Subscription(institutionId, pkg, false, startDate);
        billingDAO.saveOrUpdateSubscription(sub);
        return sub;
    }

    /**
     * Upgrades an institution to a higher bandwidth package.
     * Rule: Offers a 10% discount on the cost of the bandwidth they are upgrading to.
     */
    public Subscription upgradeSubscription(int institutionId, BandwidthPackage newPkg) throws SQLException {
        Subscription sub = new Subscription(institutionId, newPkg, true, LocalDate.now());
        billingDAO.saveOrUpdateSubscription(sub);
        return sub;
    }

    public Subscription getSubscription(int institutionId) throws SQLException {
        return billingDAO.findSubscriptionByInstitutionId(institutionId);
    }

    public List<Subscription> getAllSubscriptions() throws SQLException {
        return billingDAO.findAllSubscriptions();
    }

    /**
     * Generates a monthly bill for an institution for a given month and year.
     */
    public Bill generateMonthlyBill(int institutionId, int month, int year) throws SQLException {
        Subscription sub = billingDAO.findSubscriptionByInstitutionId(institutionId);
        if (sub == null) {
            throw new IllegalStateException("Institution has no active bandwidth subscription.");
        }

        double charge = sub.getFinalMonthlyCost();
        Bill bill = new Bill(institutionId, month, year, charge);
        billingDAO.saveBill(bill);
        return bill;
    }

    /**
     * Evaluates all bills against an evaluation date to update statuses:
     * - Past end of month: marks OVERDUE, applies 15% fine (Defaulter)
     * - Past 10th of next month: marks DISCONNECTED (Disconnection issue)
     */
    public void evaluateBillsStatus(LocalDate evaluationDate) throws SQLException {
        List<Bill> bills = billingDAO.findAllBills();
        for (Bill bill : bills) {
            if ("PAID".equalsIgnoreCase(bill.getStatus())) {
                continue;
            }
            String prevStatus = bill.getStatus();
            bill.evaluateStatus(evaluationDate);
            if (!bill.getStatus().equalsIgnoreCase(prevStatus)) {
                billingDAO.updateBill(bill);
                institutionDAO.updateStatus(bill.getInstitutionId(), bill.getStatus());
            }
        }
    }

    /**
     * Applies re-connection surcharge of KSh 1,000 to a disconnected institution.
     */
    public void applyReconnection(int billId) throws SQLException {
        List<Bill> bills = billingDAO.findAllBills();
        for (Bill bill : bills) {
            if (bill.getId() == billId) {
                bill.applyReconnectionFee();
                billingDAO.updateBill(bill);
                break;
            }
        }
    }

    public List<Bill> getAllBills() throws SQLException {
        return billingDAO.findAllBills();
    }

    public List<Bill> getDefaulterBills() throws SQLException {
        return billingDAO.findDefaulterBills();
    }

    public List<Bill> getDisconnectedBills() throws SQLException {
        return billingDAO.findDisconnectedBills();
    }

    public List<Bill> getBillsForInstitution(int institutionId) throws SQLException {
        return billingDAO.findBillsByInstitutionId(institutionId);
    }
}

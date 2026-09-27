package com.azani.isp.service;

import com.azani.isp.dao.BillingDAO;
import com.azani.isp.dao.InstitutionDAO;
import com.azani.isp.dao.PaymentDAO;
import com.azani.isp.model.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for capturing payments:
 * - Registration fees (KSh 8,500)
 * - Installation fees
 * - Monthly internet payments
 * - Reconnection fees
 */
public class PaymentService {
    private final PaymentDAO paymentDAO;
    private final BillingDAO billingDAO;
    private final InstitutionDAO institutionDAO;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
        this.billingDAO = new BillingDAO();
        this.institutionDAO = new InstitutionDAO();
    }

    public PaymentService(PaymentDAO paymentDAO, BillingDAO billingDAO, InstitutionDAO institutionDAO) {
        this.paymentDAO = paymentDAO;
        this.billingDAO = billingDAO;
        this.institutionDAO = institutionDAO;
    }

    /**
     * Captures registration fee payment of KSh 8,500 (Task 2a).
     */
    public Payment captureRegistrationFee(int institutionId, String refNo, String notes) throws SQLException {
        if (refNo == null || refNo.trim().isEmpty()) {
            refNo = "REG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        Payment payment = new Payment(
                institutionId,
                PaymentType.REGISTRATION,
                Institution.REGISTRATION_FEE,
                LocalDateTime.now(),
                refNo,
                notes != null ? notes : "Registration Fee Payment"
        );
        paymentDAO.savePayment(payment);
        return payment;
    }

    /**
     * Captures installation fee payment (Task 2b).
     */
    public Payment captureInstallationFee(int institutionId, double amount, String refNo, String notes) throws SQLException {
        if (refNo == null || refNo.trim().isEmpty()) {
            refNo = "INST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        Payment payment = new Payment(
                institutionId,
                PaymentType.INSTALLATION,
                amount,
                LocalDateTime.now(),
                refNo,
                notes != null ? notes : "Installation and Equipment Fee Payment"
        );
        paymentDAO.savePayment(payment);
        return payment;
    }

    /**
     * Captures monthly internet bill payment (Task 2c), reconciling outstanding balance.
     */
    public Payment captureMonthlyPayment(int billId, double amount, String refNo, String notes) throws SQLException {
        List<Bill> bills = billingDAO.findAllBills();
        Bill targetBill = null;
        for (Bill b : bills) {
            if (b.getId() == billId) {
                targetBill = b;
                break;
            }
        }

        if (targetBill == null) {
            throw new IllegalArgumentException("Bill not found with ID: " + billId);
        }

        if (refNo == null || refNo.trim().isEmpty()) {
            refNo = "MTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        Payment payment = new Payment(
                targetBill.getInstitutionId(),
                PaymentType.MONTHLY,
                amount,
                LocalDateTime.now(),
                refNo,
                notes != null ? notes : ("Monthly Internet Payment for Month " + targetBill.getBillingMonth() + "/" + targetBill.getBillingYear())
        );
        paymentDAO.savePayment(payment);

        targetBill.setAmountPaid(targetBill.getAmountPaid() + amount);
        if (targetBill.getOutstandingBalance() <= 0.0) {
            targetBill.setStatus("PAID");
            institutionDAO.updateStatus(targetBill.getInstitutionId(), "ACTIVE");
        }
        billingDAO.updateBill(targetBill);

        return payment;
    }

    /**
     * Captures re-connection surcharge fee of KSh 1,000.
     */
    public Payment captureReconnectionFee(int institutionId, String refNo, String notes) throws SQLException {
        if (refNo == null || refNo.trim().isEmpty()) {
            refNo = "REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        Payment payment = new Payment(
                institutionId,
                PaymentType.RECONNECTION,
                Bill.RECONNECTION_FEE,
                LocalDateTime.now(),
                refNo,
                notes != null ? notes : "Reconnection Surcharge Payment"
        );
        paymentDAO.savePayment(payment);
        institutionDAO.updateStatus(institutionId, "ACTIVE");
        return payment;
    }

    public List<Payment> getAllPayments() throws SQLException {
        return paymentDAO.findAll();
    }

    public List<Payment> getPaymentsForInstitution(int institutionId) throws SQLException {
        return paymentDAO.findByInstitutionId(institutionId);
    }
}

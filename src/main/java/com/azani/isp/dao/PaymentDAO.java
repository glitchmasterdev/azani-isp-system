package com.azani.isp.dao;

import com.azani.isp.model.Payment;
import com.azani.isp.model.PaymentType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Payment records.
 */
public class PaymentDAO {

    public int savePayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (institution_id, payment_type, amount, payment_date, reference_no, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getInstitutionId());
            ps.setString(2, payment.getPaymentType().name());
            ps.setDouble(3, payment.getAmount());
            ps.setTimestamp(4, Timestamp.valueOf(payment.getPaymentDate()));
            ps.setString(5, payment.getReferenceNo());
            ps.setString(6, payment.getNotes());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    payment.setId(rs.getInt(1));
                    return payment.getId();
                }
            }
        }
        return 0;
    }

    public List<Payment> findAll() throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPayment(rs));
            }
        }
        return list;
    }

    public List<Payment> findByInstitutionId(int institutionId) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE institution_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, institutionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPayment(rs));
                }
            }
        }
        return list;
    }

    public double getTotalPaidForInstitution(int institutionId, PaymentType type) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE institution_id = ? AND payment_type = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, institutionId);
            ps.setString(2, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("payment_date");
        return new Payment(
                rs.getInt("id"),
                rs.getInt("institution_id"),
                PaymentType.fromString(rs.getString("payment_type")),
                rs.getDouble("amount"),
                ts != null ? ts.toLocalDateTime() : null,
                rs.getString("reference_no"),
                rs.getString("notes")
        );
    }
}

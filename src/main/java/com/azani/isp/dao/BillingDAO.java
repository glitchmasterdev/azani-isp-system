package com.azani.isp.dao;

import com.azani.isp.model.BandwidthPackage;
import com.azani.isp.model.Bill;
import com.azani.isp.model.Subscription;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Subscriptions and Bills.
 */
public class BillingDAO {

    // --- Subscription Operations ---

    public void saveOrUpdateSubscription(Subscription sub) throws SQLException {
        String checkSql = "SELECT id FROM subscriptions WHERE institution_id = ?";
        String insertSql = "INSERT INTO subscriptions (institution_id, bandwidth_mbps, base_monthly_cost, " +
                "is_upgraded, discount_percent, final_monthly_cost, start_date, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE subscriptions SET bandwidth_mbps = ?, base_monthly_cost = ?, " +
                "is_upgraded = ?, discount_percent = ?, final_monthly_cost = ?, start_date = ?, active = ? WHERE institution_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean exists = false;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, sub.getInstitutionId());
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) exists = true;
                }
            }

            if (exists) {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, sub.getBandwidthPackage().getSpeedMbps());
                    ps.setDouble(2, sub.getBaseMonthlyCost());
                    ps.setBoolean(3, sub.isUpgraded());
                    ps.setDouble(4, sub.getDiscountPercent());
                    ps.setDouble(5, sub.getFinalMonthlyCost());
                    ps.setDate(6, Date.valueOf(sub.getStartDate()));
                    ps.setBoolean(7, sub.isActive());
                    ps.setInt(8, sub.getInstitutionId());
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, sub.getInstitutionId());
                    ps.setInt(2, sub.getBandwidthPackage().getSpeedMbps());
                    ps.setDouble(3, sub.getBaseMonthlyCost());
                    ps.setBoolean(4, sub.isUpgraded());
                    ps.setDouble(5, sub.getDiscountPercent());
                    ps.setDouble(6, sub.getFinalMonthlyCost());
                    ps.setDate(7, Date.valueOf(sub.getStartDate()));
                    ps.setBoolean(8, sub.isActive());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) sub.setId(rs.getInt(1));
                    }
                }
            }
        }
    }

    public Subscription findSubscriptionByInstitutionId(int institutionId) throws SQLException {
        String sql = "SELECT * FROM subscriptions WHERE institution_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, institutionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSubscription(rs);
                }
            }
        }
        return null;
    }

    public List<Subscription> findAllSubscriptions() throws SQLException {
        List<Subscription> list = new ArrayList<>();
        String sql = "SELECT * FROM subscriptions ORDER BY institution_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSubscription(rs));
            }
        }
        return list;
    }

    private Subscription mapResultSetToSubscription(ResultSet rs) throws SQLException {
        int speed = rs.getInt("bandwidth_mbps");
        BandwidthPackage pkg = BandwidthPackage.fromSpeed(speed);
        Date startDate = rs.getDate("start_date");
        return new Subscription(
                rs.getInt("id"),
                rs.getInt("institution_id"),
                pkg,
                rs.getDouble("base_monthly_cost"),
                rs.getBoolean("is_upgraded"),
                rs.getDouble("discount_percent"),
                rs.getDouble("final_monthly_cost"),
                startDate != null ? startDate.toLocalDate() : null,
                rs.getBoolean("active")
        );
    }

    // --- Bill Operations ---

    public int saveBill(Bill bill) throws SQLException {
        String sql = "INSERT INTO bills (institution_id, billing_month, billing_year, base_charge, overdue_fine, " +
                "reconnection_fee, total_due, amount_paid, due_date, disconnection_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bill.getInstitutionId());
            ps.setInt(2, bill.getBillingMonth());
            ps.setInt(3, bill.getBillingYear());
            ps.setDouble(4, bill.getBaseCharge());
            ps.setDouble(5, bill.getOverdueFine());
            ps.setDouble(6, bill.getReconnectionFee());
            ps.setDouble(7, bill.getTotalDue());
            ps.setDouble(8, bill.getAmountPaid());
            ps.setDate(9, Date.valueOf(bill.getDueDate()));
            ps.setDate(10, Date.valueOf(bill.getDisconnectionDate()));
            ps.setString(11, bill.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    bill.setId(rs.getInt(1));
                    return bill.getId();
                }
            }
        }
        return 0;
    }

    public void updateBill(Bill bill) throws SQLException {
        String sql = "UPDATE bills SET base_charge = ?, overdue_fine = ?, reconnection_fee = ?, " +
                "total_due = ?, amount_paid = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, bill.getBaseCharge());
            ps.setDouble(2, bill.getOverdueFine());
            ps.setDouble(3, bill.getReconnectionFee());
            ps.setDouble(4, bill.getTotalDue());
            ps.setDouble(5, bill.getAmountPaid());
            ps.setString(6, bill.getStatus());
            ps.setInt(7, bill.getId());
            ps.executeUpdate();
        }
    }

    public List<Bill> findAllBills() throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToBill(rs));
            }
        }
        return list;
    }

    public List<Bill> findBillsByInstitutionId(int institutionId) throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE institution_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, institutionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBill(rs));
                }
            }
        }
        return list;
    }

    public List<Bill> findDefaulterBills() throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE status IN ('OVERDUE', 'DISCONNECTED') ORDER BY institution_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToBill(rs));
            }
        }
        return list;
    }

    public List<Bill> findDisconnectedBills() throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE status = 'DISCONNECTED' ORDER BY institution_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToBill(rs));
            }
        }
        return list;
    }

    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Date due = rs.getDate("due_date");
        Date disconn = rs.getDate("disconnection_date");
        return new Bill(
                rs.getInt("id"),
                rs.getInt("institution_id"),
                rs.getInt("billing_month"),
                rs.getInt("billing_year"),
                rs.getDouble("base_charge"),
                rs.getDouble("overdue_fine"),
                rs.getDouble("reconnection_fee"),
                rs.getDouble("total_due"),
                rs.getDouble("amount_paid"),
                due != null ? due.toLocalDate() : null,
                disconn != null ? disconn.toLocalDate() : null,
                rs.getString("status")
        );
    }
}

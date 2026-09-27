package com.azani.isp.dao;

import com.azani.isp.model.Infrastructure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Infrastructure and Site Readiness Requirements.
 */
public class InfrastructureDAO {

    public void saveOrUpdate(Infrastructure infra) throws SQLException {
        String checkSql = "SELECT id FROM infrastructure_requirements WHERE institution_id = ?";
        String insertSql = "INSERT INTO infrastructure_requirements (institution_id, num_users, is_ready, pcs_purchased, " +
                "lan_nodes, pc_cost, lan_cost, base_installation_fee, total_installation_cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE infrastructure_requirements SET num_users = ?, is_ready = ?, pcs_purchased = ?, " +
                "lan_nodes = ?, pc_cost = ?, lan_cost = ?, base_installation_fee = ?, total_installation_cost = ? WHERE institution_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean exists = false;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, infra.getInstitutionId());
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        exists = true;
                    }
                }
            }

            if (exists) {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, infra.getNumUsers());
                    ps.setBoolean(2, infra.isReady());
                    ps.setInt(3, infra.getPcsPurchased());
                    ps.setInt(4, infra.getLanNodes());
                    ps.setDouble(5, infra.getPcCost());
                    ps.setDouble(6, infra.getLanCost());
                    ps.setDouble(7, infra.getBaseInstallationFee());
                    ps.setDouble(8, infra.getTotalInstallationCost());
                    ps.setInt(9, infra.getInstitutionId());
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, infra.getInstitutionId());
                    ps.setInt(2, infra.getNumUsers());
                    ps.setBoolean(3, infra.isReady());
                    ps.setInt(4, infra.getPcsPurchased());
                    ps.setInt(5, infra.getLanNodes());
                    ps.setDouble(6, infra.getPcCost());
                    ps.setDouble(7, infra.getLanCost());
                    ps.setDouble(8, infra.getBaseInstallationFee());
                    ps.setDouble(9, infra.getTotalInstallationCost());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            infra.setId(rs.getInt(1));
                        }
                    }
                }
            }
        }
    }

    public Infrastructure findByInstitutionId(int institutionId) throws SQLException {
        String sql = "SELECT * FROM infrastructure_requirements WHERE institution_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, institutionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInfrastructure(rs);
                }
            }
        }
        return null;
    }

    public List<Infrastructure> findAll() throws SQLException {
        List<Infrastructure> list = new ArrayList<>();
        String sql = "SELECT * FROM infrastructure_requirements ORDER BY institution_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToInfrastructure(rs));
            }
        }
        return list;
    }

    private Infrastructure mapResultSetToInfrastructure(ResultSet rs) throws SQLException {
        return new Infrastructure(
                rs.getInt("id"),
                rs.getInt("institution_id"),
                rs.getInt("num_users"),
                rs.getBoolean("is_ready"),
                rs.getInt("pcs_purchased"),
                rs.getInt("lan_nodes"),
                rs.getDouble("pc_cost"),
                rs.getDouble("lan_cost"),
                rs.getDouble("base_installation_fee"),
                rs.getDouble("total_installation_cost")
        );
    }
}

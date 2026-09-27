package com.azani.isp.dao;

import com.azani.isp.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Institutions and Contact Persons.
 */
public class InstitutionDAO {

    public int saveInstitution(Institution institution, ContactPerson contact) throws SQLException {
        String sqlInst = "INSERT INTO institutions (name, category, physical_address, registration_date, status) VALUES (?, ?, ?, ?, ?)";
        String sqlContact = "INSERT INTO contact_persons (institution_id, full_name, designation, phone, email, national_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            int instId;

            try (PreparedStatement psInst = conn.prepareStatement(sqlInst, Statement.RETURN_GENERATED_KEYS)) {
                psInst.setString(1, institution.getName());
                psInst.setString(2, institution.getCategory().name());
                psInst.setString(3, institution.getPhysicalAddress());
                psInst.setDate(4, Date.valueOf(institution.getRegistrationDate()));
                psInst.setString(5, institution.getStatus());
                psInst.executeUpdate();

                try (ResultSet rs = psInst.getGeneratedKeys()) {
                    if (rs.next()) {
                        instId = rs.getInt(1);
                        institution.setId(instId);
                    } else {
                        conn.rollback();
                        throw new SQLException("Failed to retrieve generated institution ID.");
                    }
                }
            }

            if (contact != null) {
                try (PreparedStatement psContact = conn.prepareStatement(sqlContact)) {
                    psContact.setInt(1, instId);
                    psContact.setString(2, contact.getFullName());
                    psContact.setString(3, contact.getDesignation());
                    psContact.setString(4, contact.getPhone());
                    psContact.setString(5, contact.getEmail());
                    psContact.setString(6, contact.getNationalId());
                    psContact.executeUpdate();
                    contact.setInstitutionId(instId);
                    institution.setContactPerson(contact);
                }
            }

            conn.commit();
            return instId;
        }
    }

    public List<Institution> findAll() throws SQLException {
        List<Institution> list = new ArrayList<>();
        String sql = "SELECT i.*, c.id AS contact_id, c.full_name, c.designation, c.phone, c.email, c.national_id " +
                     "FROM institutions i LEFT JOIN contact_persons c ON i.id = c.institution_id ORDER BY i.id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Institution inst = mapResultSetToInstitution(rs);
                list.add(inst);
            }
        }
        return list;
    }

    public Institution findById(int id) throws SQLException {
        String sql = "SELECT i.*, c.id AS contact_id, c.full_name, c.designation, c.phone, c.email, c.national_id " +
                     "FROM institutions i LEFT JOIN contact_persons c ON i.id = c.institution_id WHERE i.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInstitution(rs);
                }
            }
        }
        return null;
    }

    public void updateStatus(int institutionId, String status) throws SQLException {
        String sql = "UPDATE institutions SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, institutionId);
            ps.executeUpdate();
        }
    }

    private Institution mapResultSetToInstitution(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        InstitutionCategory category = InstitutionCategory.fromString(rs.getString("category"));
        String address = rs.getString("physical_address");
        Date regDate = rs.getDate("registration_date");
        String status = rs.getString("status");

        Institution inst = InstitutionFactory.createInstitution(
                id, name, category, address,
                regDate != null ? regDate.toLocalDate() : null, status
        );

        int contactId = rs.getInt("contact_id");
        if (contactId > 0) {
            ContactPerson contact = new ContactPerson(
                    contactId, id,
                    rs.getString("full_name"),
                    rs.getString("designation"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("national_id")
            );
            inst.setContactPerson(contact);
        }

        return inst;
    }
}

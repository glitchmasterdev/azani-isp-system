package com.azani.isp.service;

import com.azani.isp.dao.BillingDAO;
import com.azani.isp.dao.InfrastructureDAO;
import com.azani.isp.dao.InstitutionDAO;
import com.azani.isp.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service handling Institution registration and profile retrieval (Task 1).
 */
public class InstitutionService {
    private final InstitutionDAO institutionDAO;
    private final InfrastructureDAO infrastructureDAO;
    private final BillingDAO billingDAO;

    public InstitutionService() {
        this.institutionDAO = new InstitutionDAO();
        this.infrastructureDAO = new InfrastructureDAO();
        this.billingDAO = new BillingDAO();
    }

    public InstitutionService(InstitutionDAO institutionDAO, InfrastructureDAO infrastructureDAO, BillingDAO billingDAO) {
        this.institutionDAO = institutionDAO;
        this.infrastructureDAO = infrastructureDAO;
        this.billingDAO = billingDAO;
    }

    /**
     * Registers a new institution along with the personal details of the contact person (Task 1).
     */
    public Institution registerInstitution(String name, InstitutionCategory category, String address,
                                           LocalDate regDate, String contactName, String designation,
                                           String phone, String email, String nationalId) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Institution name cannot be blank.");
        }
        if (contactName == null || contactName.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact person name cannot be blank.");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact phone number is required.");
        }

        Institution institution = InstitutionFactory.createNewInstitution(name.trim(), category, address.trim(), regDate != null ? regDate : LocalDate.now());
        ContactPerson contact = new ContactPerson(contactName.trim(), designation.trim(), phone.trim(), email.trim(), nationalId.trim());

        int id = institutionDAO.saveInstitution(institution, contact);
        institution.setId(id);
        return institution;
    }

    public List<Institution> getAllInstitutions() throws SQLException {
        List<Institution> institutions = institutionDAO.findAll();
        for (Institution inst : institutions) {
            inst.setInfrastructure(infrastructureDAO.findByInstitutionId(inst.getId()));
            inst.setSubscription(billingDAO.findSubscriptionByInstitutionId(inst.getId()));
        }
        return institutions;
    }

    public Institution getInstitutionById(int id) throws SQLException {
        Institution inst = institutionDAO.findById(id);
        if (inst != null) {
            inst.setInfrastructure(infrastructureDAO.findByInstitutionId(id));
            inst.setSubscription(billingDAO.findSubscriptionByInstitutionId(id));
        }
        return inst;
    }

    public void updateStatus(int institutionId, String status) throws SQLException {
        institutionDAO.updateStatus(institutionId, status);
    }

    /**
     * Permanently removes an institution and all associated data (CASCADE).
     * Used when a member revokes their membership.
     */
    public void revokeInstitution(int institutionId) throws SQLException {
        institutionDAO.deleteById(institutionId);
    }
}

package com.azani.isp.service;

import com.azani.isp.dao.InfrastructureDAO;
import com.azani.isp.model.Infrastructure;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for managing Infrastructure requirements, site readiness assessments,
 * and cost calculations for PCs and LAN nodes.
 */
public class InfrastructureService {
    private final InfrastructureDAO infrastructureDAO;

    public InfrastructureService() {
        this.infrastructureDAO = new InfrastructureDAO();
    }

    public InfrastructureService(InfrastructureDAO infrastructureDAO) {
        this.infrastructureDAO = infrastructureDAO;
    }

    /**
     * Records or updates infrastructure assessment for an institution.
     */
    public Infrastructure assessInfrastructure(int institutionId, int numUsers, boolean ready,
                                               int pcsPurchased, int lanNodes) throws SQLException {
        Infrastructure infra = new Infrastructure(institutionId, numUsers, ready, pcsPurchased, lanNodes);
        infrastructureDAO.saveOrUpdate(infra);
        return infra;
    }

    public Infrastructure getInfrastructure(int institutionId) throws SQLException {
        return infrastructureDAO.findByInstitutionId(institutionId);
    }

    public List<Infrastructure> getAllInfrastructure() throws SQLException {
        return infrastructureDAO.findAll();
    }
}

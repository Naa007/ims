package com.stepup.ims.repository;

import com.stepup.ims.model.BusinessStats;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class StatsRepository {

    private final EntityManager entityManager;

    public StatsRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public BusinessStats getBusinessStats() {
        String sqlQuery = "SELECT " +
                // Employee stats
                "(SELECT COUNT(e.emp_id) FROM employees e) AS totalEmployees, " +
                "(SELECT COUNT(e.emp_id) FROM employees e WHERE e.active = 'YES') AS activeEmployees, " +
                "(SELECT COUNT(e.emp_id) FROM employees e WHERE e.role = 'COORDINATOR') AS coordinatorEmployees, " +
                "(SELECT COUNT(e.emp_id) FROM employees e WHERE e.role = 'TECHNICAL_COORDINATOR') AS technicalCoordinatorEmployees, " +

                // Client stats
                "(SELECT COUNT(c.client_id) FROM client c) AS totalClients, " +
                "(SELECT COUNT(c.client_id) FROM client c WHERE c.confirmation_date > DATE_SUB(CURRENT_DATE, INTERVAL 365 DAY)) AS newClientsLastYear, " +

                // Inspector stats
                "(SELECT COUNT(i.inspector_id) FROM inspector i) AS totalInspectors, " +
                "(SELECT COUNT(i.inspector_id) FROM inspector i WHERE i.inspector_status = 'ACTIVE') AS activeInspectors, " +
                "(SELECT COUNT(i.inspector_id) FROM inspector i WHERE i.inspector_status = 'INACTIVE') AS inactiveInspectors, " +
                "(SELECT COUNT(i.inspector_id) FROM inspector i WHERE i.inspector_status = 'DELETED') AS deletedInspectors, " +
                "(SELECT COUNT(i.inspector_id) FROM inspector i WHERE i.inspector_type = 0) AS inhouseInspectors, " +
                "(SELECT COUNT(i.inspector_id) FROM inspector i WHERE i.inspector_type = 3) AS partnerInspectors, " +
                "(SELECT COUNT(i.inspector_id) FROM inspector i WHERE i.inspector_type = 2) AS freelancerInspectors, " +

                // Inspection stats
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins) AS totalInspections, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_reports_received_date IS NOT NULL) AS completedInspections, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_reports_received_date IS NULL " +
                "AND EXISTS (SELECT 1 FROM proposed_cvs pc WHERE pc.inspection_id = ins.inspection_id AND pc.cv_status = 1)) AS ongoingInspections, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_reports_received_date IS NULL " +
                "AND NOT EXISTS (SELECT 1 FROM proposed_cvs pc WHERE pc.inspection_id = ins.inspection_id AND pc.cv_status = 1)) AS pendingInspections " +
                "FROM DUAL";

        Query query = entityManager.createNativeQuery(sqlQuery, BusinessStats.class);
        return (BusinessStats) query.getSingleResult();
    }
}
package com.stepup.ims.repository;

import com.stepup.ims.model.BusinessStats;
import com.stepup.ims.model.IndividualStats;
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
                "AND NOT EXISTS (SELECT 1 FROM proposed_cvs pc WHERE pc.inspection_id = ins.inspection_id AND pc.cv_status = 1)) AS pendingInspections, " +
                // Inspection status counts
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '0') AS newInspections, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '1') AS inspectorAssigned, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '2') AS inspectorReviewAwaiting, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '3') AS inspectorReviewCompleted, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '4') AS inspectorApproved, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '5') AS referenceDocReceived, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '6') AS referenceDocReviewAwaiting, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '7') AS referenceDocReviewCompleted, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '8') AS inspectionReportsReceived, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '9') AS inspectionReportsReviewAwaiting, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '10') AS inspectionReportsReviewCompleted, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '11') AS inspectionReportsSentToClient, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '12') AS inspectionAwarded, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '13') AS inspectionRejected, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.inspection_status = '14') AS closedInspections " +
                "FROM DUAL";

        Query query = entityManager.createNativeQuery(sqlQuery, BusinessStats.class);
        return (BusinessStats) query.getSingleResult();
    }
    public IndividualStats getIndividualStats(String email) {
        String sqlQuery = "SELECT " +
                // Total inspections for this coordinator
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email) AS totalInspections, " +

                // Inspection status counts for this coordinator
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '0') AS newInspections, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '1') AS inspectorAssigned, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '2') AS inspectorReviewAwaiting, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '3') AS inspectorReviewCompleted, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '4') AS inspectorApproved, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '5') AS referenceDocReceived, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '6') AS referenceDocReviewAwaiting, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '7') AS referenceDocReviewCompleted, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '8') AS inspectionReportsReceived, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '9') AS inspectionReportsReviewAwaiting, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '10') AS inspectionReportsReviewCompleted, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '11') AS inspectionReportsSentToClient, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '12') AS awardedInspections, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '13') AS rejectedInspections, " +
                "(SELECT COUNT(ins.inspection_id) FROM inspection ins WHERE ins.created_by = :email AND ins.inspection_status = '14') AS closedInspections " +
                "FROM DUAL";

        Query query = entityManager.createNativeQuery(sqlQuery)
                .setParameter("email", email);

        Object[] result = (Object[]) query.getSingleResult();

        return new IndividualStats(
                ((Number) result[0]).longValue(),
                ((Number) result[1]).longValue(),
                ((Number) result[2]).longValue(),
                ((Number) result[3]).longValue(),
                ((Number) result[4]).longValue(),
                ((Number) result[5]).longValue(),
                ((Number) result[6]).longValue(),
                ((Number) result[7]).longValue(),
                ((Number) result[8]).longValue(),
                ((Number) result[9]).longValue(),
                ((Number) result[10]).longValue(),
                ((Number) result[11]).longValue(),
                ((Number) result[12]).longValue(),
                ((Number) result[13]).longValue(),
                ((Number) result[14]).longValue(),
                ((Number) result[15]).longValue()
        );
    }
}
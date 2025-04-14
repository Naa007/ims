package com.stepup.ims.service;

import com.stepup.ims.entity.Inspection;
import com.stepup.ims.model.BusinessStats;
import com.stepup.ims.model.InpsectionStatsByRole;
import com.stepup.ims.repository.InspectionRepository;
import com.stepup.ims.repository.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {

    private final StatsRepository statsRepository;

    @Autowired
    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }
    
    @Autowired
    private InspectionRepository inspectionRepository;

    public Map<String, Object> getBusinessStats() {
        BusinessStats stats = statsRepository.getBusinessStats();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("Employee Stats", createEmployeeStats(stats));
        result.put("Client Stats", createClientStats(stats));
        result.put("Inspector Stats", createInspectorStats(stats));
        result.put("Inspection Stats", createInspectionStats(stats));
        result.put("Inspection Status Stats", createInspectionStatusStats(stats));

        return result;
    }

    private Map<String, Object> createEmployeeStats(BusinessStats stats) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Total Employees", stats.getTotalEmployees());
        map.put("Active Employees", stats.getActiveEmployees());
        map.put("Coordinators", stats.getCoordinators());
        map.put("Technical Coordinators", stats.getTechnicalCoordinators());
        return map;
    }

    private Map<String, Object> createClientStats(BusinessStats stats) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Total Clients", stats.getTotalClients());
        map.put("Recently Onboarded", stats.getRecentlyOnboardedClients());
        return map;
    }

    private Map<String, Object> createInspectorStats(BusinessStats stats) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Total Inspectors", stats.getTotalInspectors());
        map.put("Active Inspectors", stats.getActiveInspectors());
        map.put("Inactive Inspectors", stats.getInactiveInspectors());
        map.put("Inhouse Inspectors", stats.getInHouseInspectors());
        map.put("Partner Inspectors", stats.getPartnerInspectors());
        map.put("Freelancer Inspectors", stats.getFreelancerInspectors());
        return map;
    }

    private Map<String, Object> createInspectionStats(BusinessStats stats) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Total Inspections", stats.getTotalInspections());
        map.put("New", stats.getNewInspections());

        // Calculate ongoing inspections by summing all intermediate statuses
        long ongoing = stats.getInspectorAssigned()
                + stats.getInspectorReviewAwaiting()
                + stats.getInspectorReviewCompleted()
                + stats.getInspectorApproved()
                + stats.getReferenceDocReceived()
                + stats.getReferenceDocReviewAwaiting()
                + stats.getReferenceDocReviewCompleted()
                + stats.getInspectionReportsReceived()
                + stats.getInspectionReportsReviewAwaiting()
                + stats.getInspectionReportsReviewCompleted()
                + stats.getInspectionReportsSentToClient();
        map.put("Ongoing", ongoing);

        map.put("Awarded", stats.getInspectionAwarded());
        map.put("Rejected", stats.getInspectionRejected());
        map.put("Closed", stats.getClosedInspections());
        return map;
    }
    private Map<String, Object> createInspectionStatusStats(BusinessStats stats) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("New", stats.getNewInspections());
        map.put("Inspector Assigned", stats.getInspectorAssigned());
        map.put("Inspector Approved", stats.getInspectorApproved());
        map.put("Inspection Reports Received", stats.getInspectionReportsReceived());
        map.put("Inspection Reports Sent to Client", stats.getInspectionReportsSentToClient());
        map.put("Inspection Awarded", stats.getInspectionAwarded());
        map.put("Inspection Rejected", stats.getInspectionRejected());
        return map;
    }

    public InpsectionStatsByRole getCoordinatorStats(String email, String period) {
        List<Inspection> inspections = inspectionRepository.findByCreatedBy(email);
        return getInspectionStatsByRole(inspections, period);
    }

    public InpsectionStatsByRole getInspectorStats(String email, String period) {
        List<Inspection> inspections = inspectionRepository.findByProposedCVs_Inspector_Email(email);
        return getInspectionStatsByRole(inspections, period);
    }

    public InpsectionStatsByRole getTechnicalCoordinatorStats(String empId, String period) {
        List<Inspection> inspections = inspectionRepository.findByProposedCVs_CvReviewBytechnicalCoordinator_EmpIdOrDocumentsReviewedByTechnicalCoordinatorOrInspectionReviewedBy(empId, empId, empId);

        return getInspectionStatsByRole(inspections, period);
    }


    public InpsectionStatsByRole getInspectionStatsByRole( List<Inspection> inspections, String period) {

        inspections = inspections.stream()
                .filter(inspection -> inspection.getCreatedDate() != null)
                .filter(inspection -> {
                    if ("WEEK".equalsIgnoreCase(period)) {
                        return !inspection.getCreatedDate().isBefore(LocalDateTime.now().minusWeeks(1));
                    } else if ("MONTH".equalsIgnoreCase(period)) {
                        return !inspection.getCreatedDate().isBefore(LocalDateTime.now().minusMonths(1));
                    } else if ("YEAR".equalsIgnoreCase(period)) {
                        return !inspection.getCreatedDate().isBefore(LocalDateTime.now().minusYears(1));
                    }
                    return true; // Default case for any other PeriodType
                })
                .toList();

        long totalInspections = inspections.size();
        long newInspections = inspections.stream().filter(inspection -> "NEW".equalsIgnoreCase(inspection.getInspectionStatus().toString())).count();
        long completedInspections = inspections.stream().filter(inspection -> "INSPECTION_AWARDED".equalsIgnoreCase(inspection.getInspectionStatus().toString())).count();
        long ongoingInspections = inspections.stream()
                .filter(inspection -> {
                    String status = inspection.getInspectionStatus().toString();
                    return "INSPECTOR_ASSIGNED".equalsIgnoreCase(status) ||
                            "INSPECTOR_REVIEW_AWAITING".equalsIgnoreCase(status) ||
                            "INSPECTOR_REVIEW_COMPLETED".equalsIgnoreCase(status) ||
                            "INSPECTOR_APPROVED".equalsIgnoreCase(status) ||
                            "REFERENCE_DOC_RECEIVED".equalsIgnoreCase(status) ||
                            "REFERENCE_DOC_REVIEW_AWAITING".equalsIgnoreCase(status) ||
                            "REFERENCE_DOC_REVIEW_COMPLETED".equalsIgnoreCase(status) ||
                            "INSPECTION_REPORTS_RECEIVED".equalsIgnoreCase(status) ||
                            "INSPECTION_REPORTS_REVIEW_AWAITING".equalsIgnoreCase(status) ||
                            "INSPECTION_REPORTS_REVIEW_COMPLETED".equalsIgnoreCase(status) ||
                            "INSPECTION_REPORTS_SENT_TO_CLIENT".equalsIgnoreCase(status);
                })
                .count();
        long rejectedInspections = inspections.stream().filter(inspection -> "INSPECTION_REJECTED".equalsIgnoreCase(inspection.getInspectionStatus().toString())).count();


        return new InpsectionStatsByRole(totalInspections, newInspections, completedInspections, ongoingInspections, rejectedInspections, InpsectionStatsByRole.PeriodType.valueOf(period.toUpperCase()));
    }
}
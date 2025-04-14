package com.stepup.ims.service;

import com.stepup.ims.model.BusinessStats;
import com.stepup.ims.repository.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class StatsService {

    private final StatsRepository statsRepository;

    @Autowired
    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

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
}
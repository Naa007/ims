package com.stepup.ims.model;

public class BusinessStats {
    // Employee stats
    private final Long totalEmployees;
    private final Long activeEmployees;
    private final Long coordinators;
    private final Long technicalCoordinators;

    // Client stats
    private final Long totalClients;
    private final Long recentlyOnboardedClients;

    // Inspector stats
    private final Long totalInspectors;
    private final Long activeInspectors;
    private final Long inactiveInspectors;
    private final Long inHouseInspectors;
    private final Long partnerInspectors;
    private final Long freelancerInspectors;

    // Inspection stats
    private final Long totalInspections;
    private final Long completedInspections;
    private final Long ongoingInspections;
    private final Long pendingInspections;

    public BusinessStats(Long totalEmployees, Long activeEmployees, Long coordinators,
                         Long technicalCoordinators, Long totalClients, Long recentlyOnboardedClients,
                         Long totalInspectors, Long activeInspectors, Long inactiveInspectors,
                         Long blockedInspectors, Long inHouseInspectors, Long partnerInspectors,
                         Long freelancerInspectors, Long totalInspections,
                         Long completedInspections, Long ongoingInspections, Long pendingInspections) {
        this.totalEmployees = totalEmployees;
        this.activeEmployees = activeEmployees;
        this.coordinators = coordinators;
        this.technicalCoordinators = technicalCoordinators;
        this.totalClients = totalClients;
        this.recentlyOnboardedClients = recentlyOnboardedClients;
        this.totalInspectors = totalInspectors;
        this.activeInspectors = activeInspectors;
        this.inactiveInspectors = inactiveInspectors;
        this.inHouseInspectors = inHouseInspectors;
        this.partnerInspectors = partnerInspectors;
        this.freelancerInspectors = freelancerInspectors;
        this.totalInspections = totalInspections;
        this.completedInspections = completedInspections;
        this.ongoingInspections = ongoingInspections;
        this.pendingInspections = pendingInspections;
    }

    // Getters for all fields
    public Long getTotalEmployees() { return totalEmployees; }
    public Long getActiveEmployees() { return activeEmployees; }
    public Long getCoordinators() { return coordinators; }
    public Long getTechnicalCoordinators() { return technicalCoordinators; }
    public Long getTotalClients() { return totalClients; }
    public Long getRecentlyOnboardedClients() { return recentlyOnboardedClients; }
    public Long getTotalInspectors() { return totalInspectors; }
    public Long getActiveInspectors() { return activeInspectors; }
    public Long getInactiveInspectors() { return inactiveInspectors; }
    public Long getInHouseInspectors() { return inHouseInspectors; }
    public Long getPartnerInspectors() { return partnerInspectors; }
    public Long getFreelancerInspectors() { return freelancerInspectors; }
    public Long getTotalInspections() { return totalInspections; }
    public Long getCompletedInspections() { return completedInspections; }
    public Long getOngoingInspections() { return ongoingInspections; }
    public Long getPendingInspections() { return pendingInspections; }
}
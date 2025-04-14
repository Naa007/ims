package com.stepup.ims.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
    private final Long blockedInspectors;
    private final Long inHouseInspectors;
    private final Long partnerInspectors;
    private final Long freelancerInspectors;

    // Inspection stats
    private final Long totalInspections;
    private final Long completedInspections;
    private final Long ongoingInspections;
    private final Long pendingInspections;

    // Inspection status counts
    private final Long newInspections;
    private final Long inspectorAssigned;
    private final Long inspectorReviewAwaiting;
    private final Long inspectorReviewCompleted;
    private final Long inspectorApproved;
    private final Long referenceDocReceived;
    private final Long referenceDocReviewAwaiting;
    private final Long referenceDocReviewCompleted;
    private final Long inspectionReportsReceived;
    private final Long inspectionReportsReviewAwaiting;
    private final Long inspectionReportsReviewCompleted;
    private final Long inspectionReportsSentToClient;
    private final Long inspectionAwarded;
    private final Long inspectionRejected;
    private final Long closedInspections;
}
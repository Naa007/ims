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
}
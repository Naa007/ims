package com.stepup.ims.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndividualStats {
    private long totalInspections;
    private long newInspections;
    private long inspectorAssigned;
    private long inspectorReviewAwaiting;
    private long inspectorReviewCompleted;
    private long inspectorApproved;
    private long referenceDocReceived;
    private long referenceDocReviewAwaiting;
    private long referenceDocReviewCompleted;
    private long inspectionReportsReceived;
    private long inspectionReportsReviewAwaiting;
    private long inspectionReportsReviewCompleted;
    private long inspectionReportsSentToClient;
    private long awardedInspections;
    private long rejectedInspections;
    private long closedInspections;
}

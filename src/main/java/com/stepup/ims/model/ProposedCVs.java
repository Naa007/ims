package com.stepup.ims.model;

import com.stepup.ims.entity.Inspector;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
public class ProposedCVs {

    @ToString.Exclude
    private Inspector inspector;
    private boolean isCVCerticatesAvailable;
    @ToString.Exclude
    private Inspector cvReviewBytechnicalCoordinator;
    private boolean isPQRAvailable;
    private LocalDateTime cvSubmittedToClientDate;
    private boolean cvStatus;
    @ToString.Exclude
    private Inspection inspection;

}

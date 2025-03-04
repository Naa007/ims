package com.stepup.ims.model;

import com.stepup.ims.entity.Inspector;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ProposedCVs {

    @ToString.Exclude
    private Inspector inspector;
    private boolean isCVCertificatesAvailable;
    @ToString.Exclude
    private Employee cvReviewByTechnicalCoordinator;
    private boolean isPQRAvailable;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd/mm/yyyy")
    private LocalDateTime cvSubmittedToClientDate;
    private boolean cvStatus;
    @ToString.Exclude
    private Inspection inspection;

}

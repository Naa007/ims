package com.stepup.ims.model;

import com.stepup.ims.entity.Inspector;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ProposedCVs {

    private Long id;
    @ToString.Exclude
    private Inspector inspector;
    private boolean cvCertificatesAvailable;
    private String cvCertificatesLink;
    @ToString.Exclude
    private Employee cvReviewByTechnicalCoordinator;
    private boolean pqrAvailable;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime cvSubmittedToClientDate;
    private boolean cvStatus;

}

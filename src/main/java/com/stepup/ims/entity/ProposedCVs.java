package com.stepup.ims.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Filter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "proposed_cvs")
public class ProposedCVs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inspector_id", referencedColumnName = "inspector_id", unique = false)
    @ToString.Exclude
    private Inspector inspector;

    @Column(name = "cv_certificates_available")
    private boolean cvCertificatesAvailable;

    @ManyToOne
    @JoinColumn(name = "technical_coordinator_id", referencedColumnName = "emp_id", unique = false)
    @Filter(name = "technicalCoordinatorFilter", condition = "inspector_type = 'TECHNICAL_COORDINATOR'")
    @ToString.Exclude
    private Employee cvReviewBytechnicalCoordinator;

    @Column(name = "pqr_available")
    private boolean pqrAvailable;

    @Column(name = "cv_submitted_date_time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime cvSubmittedToClientDate;

    @Column(name = "cv_status")
    private boolean cvStatus;

}

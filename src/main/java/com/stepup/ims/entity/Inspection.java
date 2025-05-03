package com.stepup.ims.entity;

import com.stepup.ims.audit.Auditable;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "inspection")
public class Inspection extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspection_id")
    private Long id;

    @Column(name = "inspection_no")
    private Long inspectionNo;

    @Column(name = "notification_no")
    private String notificationNo;


    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", unique = false)
    private Client client;

    @Column(name = "notification_received_date_time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime notificationReceivedDateTime;

    @Column(name = "inspection_country")
    private String inspectionCountry;

    @ElementCollection
    @CollectionTable(name = "inspection_dates", joinColumns = @JoinColumn(name = "inspection_id"))
    @Column(name = "inspection_date_as_per_notification")
    private List<String> inspectionDateAsPerNotification;

    @Column(name = "inspection_item")
    private String inspectionItem;

    @Column(name = "inspection_activity_with_stages")
    private String inspectionActivityWithStages;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_type")
    private InspectionType inspectionType;

    @Column(name = "inspection_location_details")
    private String inspectionLocationDetails;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "inspection_id", referencedColumnName = "inspection_id")
    private List<ProposedCVs> proposedCVs;

    @Column(name = "approved_inspector_name")
    private String approvedInspectorName;

    @Column(name = "order_confirmation_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate orderConfirmationDate;

    @Column(name = "sector_scope")
    private int sectorScope;

    @Column(name = "end_client_name")
    private String endClientName;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "reference_documents_for_inspection_status")
    private boolean referenceDocumentsForInspectionStatus;

    @Column(name = "reference_documents_link")
    private String referenceDocumentsLink;

    @Column(name = "documents_reviewed_by_technical_coordinator")
    private String documentsReviewedByTechnicalCoordinator;

    @Column(name = "contract_review_prepared")
    private boolean contractReviewPrepared;

    @Column(name = "inspection_advise_note")
    private boolean inspectionAdviseNote;

    @Column(name = "instructions_to_inspector_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate instructionsToInspectorDate;

    @Column(name = "any_inspection_issues")
    private boolean anyInspectionIssues;

    @Column(name = "fr_sent_to_client_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate frSentToClientDate;

    @Column(name = "inspection_reports_received_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate inspectionReportsReceivedDate;

    @Column(name = "inspection_reviewed_by")
    private String inspectionReviewedBy;

    @Column(name = "inspection_support_documents_sent_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate inspectionSupportDocumentsSentDate;

    @Column(name = "inspection_report_number")
    private String inspectionReportNumber;

    @Column(name = "ncr_raised")
    private boolean ncrRaised;

    @Column(name = "irn_sent_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate irnSentDate;

    @Column(name = "imprint_and_confidentiality_status")
    private boolean impartialityAndConfidentiality;

    @Column(name = "job_folder_link")
    private String jobFolderLink;

    @OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
    @JoinColumn(name = "contract_review_id")
    private ContractReview contractReview;

    @OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
    @JoinColumn(name = "inspection_advise_id")
    private InspectionAdvise inspectionAdvise;

    @Column(name = "inspection_status", nullable = false)
    private InspectionStatusType inspectionStatus;

    @Column(name = "coordinator_name", updatable = false)
    private String coordinatorName;


    public enum InspectionType {
        MECHANICAL, ELECTRICAL
    }

    public enum InspectionStatusType {
        NEW,
        INSPECTOR_ASSIGNED,
        INSPECTOR_REVIEW_AWAITING,
        INSPECTOR_REVIEW_COMPLETED,
        INSPECTOR_APPROVED,
        REFERENCE_DOC_RECEIVED,
        REFERENCE_DOC_REVIEW_AWAITING,
        REFERENCE_DOC_REVIEW_COMPLETED,
        INSPECTION_REPORTS_RECEIVED,
        INSPECTION_REPORTS_REVIEW_AWAITING,
        INSPECTION_REPORTS_REVIEW_COMPLETED,
        INSPECTION_REPORTS_SENT_TO_CLIENT,
        INSPECTION_AWARDED,
        INSPECTION_REJECTED,
        CLOSED;
    }
}

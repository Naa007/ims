package com.stepup.ims.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stepup.ims.utils.DataUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Inspection {

    private Long id;

    private Long inspectionNo;

    private String notificationNo;

    private Client client;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime notificationReceivedDateTime;

    private String inspectionCountry;

    private List<String> inspectionDateAsPerNotification;

    private String inspectionItem;

    private String inspectionActivityWithStages;

    private InspectionType inspectionType;

    private String inspectionLocationDetails;

    private List<ProposedCVs> proposedCVs;

    private List<String> approvedInspectorNames;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate orderConfirmationDate;

    private int sectorScope;

    private String endClientName;

    private String projectName;

    private boolean referenceDocumentsForInspectionStatus;

    private String referenceDocumentsLink;

    private String documentsReviewedByTechnicalCoordinator;

    private boolean contractReviewPrepared;

    private boolean inspectionAdviseNote;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate instructionsToInspectorDate;

    private boolean anyInspectionIssues;

    private List<InspectionReports> inspectionReports;

    private String inspectionReportNumber;

    private boolean ncrRaised;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate irnSentDate;

    private boolean impartialityAndConfidentiality;

    private String jobFolderLink;

    private ContractReview contractReview;

    private InspectionAdvise inspectionAdvise;

    private InspectionStatusType inspectionStatus;

    private String coordinatorName;

    @JsonIgnore
    private List<String> countriesList;

    @JsonIgnore
    private List<Client> clientsList;

    @JsonIgnore
    private List<Inspector> inspectorsList;

    @JsonIgnore
    private List<Employee> technicalCoordinatorsList;


    public enum InspectionType {
        MECHANICAL, ELECTRICAL
    }

    public enum InspectionStatusType {
        NEW("New"),
        INSPECTOR_ASSIGNED("Inspector Assigned"),
        INSPECTOR_REVIEW_AWAITING( "Inspector Review Awaiting"),
        INSPECTOR_REVIEW_COMPLETED( "Inspector Review Completed"),
        INSPECTOR_APPROVED( "Inspector Approved"),
        REFERENCE_DOC_RECEIVED( "Docs Received"),
        REFERENCE_DOC_REVIEW_AWAITING("Docs Review Awaiting"),
        REFERENCE_DOC_REVIEW_COMPLETED("Docs Review Completed"),
        INSPECTION_REPORTS_RECEIVED( "Reports Received"),
        INSPECTION_REPORTS_REVIEW_AWAITING( "Reports Review Awaiting"),
        INSPECTION_REPORTS_REVIEW_COMPLETED( "Reports Review Completed"),
        INSPECTION_REPORTS_SENT_TO_CLIENT( "Reports Sent to Client"),
        INSPECTION_AWARDED( "Awarded"),
        INSPECTION_REJECTED( "Rejected"),
        CLOSED ("Closed");

        private final String description;

        InspectionStatusType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public List<String> getCountriesList() {
        return DataUtils.getCountriesList();
    }
}

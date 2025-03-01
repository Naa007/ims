package com.stepup.ims.model;

import com.stepup.ims.utils.DataUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Inspection {

    private Long id;

    private int inspectionNo;

    private String notificationNo;

    private Client client;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime notificationReceivedDateTime;

    private String inspectionCountry;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private List<LocalDate> inspectionDateAsPerNotification;

    private String inspectionItem;

    private String inspectionActivityWithStages;

    private InspectionType inspectionType;

    private String inspectionLocationDetails;

    private List<ProposedCVs> proposedCVs;

    private String approvedInspectorName;

    private LocalDateTime orderConfirmationDate;

    private int sectorScope;

    private String endClientName;

    private String projectName;

    private DocumentStatus referenceDocumentsForInspectionStatus;

    private String documentsReviewedByTechnicalCoordinator;

    private boolean contractReviewPrepared;

    private String inspectionAdviseNote;

    private LocalDateTime instructionsToInspectorDate;

    private boolean anyInspectionIssues;

    private LocalDateTime frSentToClientDate;

    private LocalDateTime inspectionReportsReceivedDate;

    private String inspectionReviewedBy;

    private LocalDateTime inspectionSupportDocumentsSentDate;

    private String inspectionReportNumber;

    private boolean ncrRaised;

    private LocalDateTime irnSentDate;

    private AvailabilityStatus impartialityAndConfidentiality;

    private String jobFolderLink;

    private List<String> countriesList;
    
    private List<Client> clientsList;

    private List<Inspector> inspectorsList;

    private List<Inspector> technicalCoordinatorsList;


    public enum InspectionType {
        MECHANICAL, ELECTRICAL
    }

    public enum DocumentStatus {
        RECEIVED, NOT_RECEIVED
    }

    public enum AvailabilityStatus {
        AVAILABLE, NOT_AVAILABLE
    }

    public List<String> getCountriesList() {
        return DataUtils.getCountriesList();
    }
}

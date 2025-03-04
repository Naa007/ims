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

    private int inspectionNo;

    private String notificationNo;

    private Client client;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime notificationReceivedDateTime;

    private String inspectionCountry;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd/MM/yyyy")
    private List<LocalDate> inspectionDateAsPerNotification;

    private String inspectionItem;

    private String inspectionActivityWithStages;

    private InspectionType inspectionType;

    private String inspectionLocationDetails;

    private List<ProposedCVs> proposedCVs;

    private String approvedInspectorName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd/MM/yyyy")
    private LocalDate orderConfirmationDate;

    private int sectorScope;

    private String endClientName;

    private String projectName;

    private boolean referenceDocumentsForInspectionStatus;

    private String documentsReviewedByTechnicalCoordinator;

    private boolean contractReviewPrepared;

    private boolean inspectionAdviseNote;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd/MM/yyyy HH:mm")
    private LocalDate instructionsToInspectorDate;

    private boolean anyInspectionIssues;


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd/MM/yyyy")
    private LocalDate frSentToClientDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd/MM/yyyy")
    private LocalDate inspectionReportsReceivedDate;

    private String inspectionReviewedBy;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd/MM/yyyy")
    private LocalDate inspectionSupportDocumentsSentDate;

    private String inspectionReportNumber;

    private boolean ncrRaised;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd/MM/yyyy")
    private LocalDate irnSentDate;

    private boolean impartialityAndConfidentiality;

    private String jobFolderLink;

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

    public List<String> getCountriesList() {
        return DataUtils.getCountriesList();
    }
}

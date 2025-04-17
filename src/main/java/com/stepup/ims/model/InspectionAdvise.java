package com.stepup.ims.model;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class InspectionAdvise {
    private Long id;
    private List<ScopeOfInspection> scopeOfInspectionList;
    private String requirement;
    private ReportingFormats reportingFormat;
    private List<ClientInstructionToInspector> clientInstructionToInspectorList;
    private String instructionsFromTechnicalTeam;
    private List<AcknowledgeFromInspector> acknowledgeFromInspectorList;
    private Long inspectionId;

    @Getter
    public enum ReportingFormats {
        IISPL, CLIENT
    }
}

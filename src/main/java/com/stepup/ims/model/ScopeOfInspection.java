package com.stepup.ims.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class ScopeOfInspection {
    private Long id;
    private ScopeOfInspectionTypes scopeOfInspection;
    private DocumentStatusTypes status;

    @Getter
    @AllArgsConstructor
    public enum ScopeOfInspectionTypes {
        DOCUMENT_REVIEW("Document Review"), SAMPLING_INSPECTION("Sampling Inspection"), STAGE_FINAL_INSPECTION("Stage or Final Inspection"), OTHERS("Others");

        @Getter
        private final String name;
    }

    @Getter
    @AllArgsConstructor
    public enum DocumentStatusTypes {
        YES("Yes"), NO("No"), NA("N/A");

        @Getter
        private final String name;
    }
}

package com.stepup.ims.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class ContractDocument {
    private Long id;
    private DocumentDescriptionTypes documentDescription;
    private DocumentStatusTypes status;
    private String specialRemarks;

    @Getter
    @AllArgsConstructor
    public enum DocumentDescriptionTypes {
        NOI("NOI"), ITP("ITP"), DATA_SHEET("Data Sheet"), PERFORMANCE_CURVE("Performance Curve"), GAD("GAD"), SHOP_INSPECTION_TEST_PROCEDURE("Shop Inspection & Test Procedure"), FR_IR_NCR_IRN_PL(""), PROCEDURES_AS_PER_ITP("Procedures as Per ITP"), OTHER("Other");

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

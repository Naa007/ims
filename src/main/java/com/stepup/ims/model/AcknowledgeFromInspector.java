package com.stepup.ims.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class AcknowledgeFromInspector {
   
    private Long id;
    private AcknowledgementTypes acknowledgement;
    private DocumentStatusTypes status;

    @Getter
    @AllArgsConstructor
    public enum AcknowledgementTypes {
        DOCS_REC("I received the documents and notification"),
        VEND_CONF("The vendor confirmed that the item will be ready as scheduled"),
        REV_REQ("Reviewed all technical requirements and understood the requirements"),
        CONF_REQ("I confirm that I have no personal connections to the vendor mentioned"),
        SAF_INS("I’ll inspect safely, take precautions, and maintain confidentiality");

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

package com.stepup.ims.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class ClientInstructionToInspector {
    private Long id;
    private ClientInstructionTypes clientInstruction;
    private DocumentStatusTypes status;

    @Getter
    @AllArgsConstructor
    public enum ClientInstructionTypes {
        INSPECTION_REPORT("Inspection Report"), FLASH_REPORT("Flash Report"), INSPECTION_RELEASE_NOTE("Inspection Release Note"), NCR ("NCR"), PUNCHLISH("Punchlish"), OTHERS("Others");

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

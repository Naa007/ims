package com.stepup.ims.entity;

import com.stepup.ims.audit.Auditable;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "client_instruction_to_inspector")
public class ClientInstructionToInspector extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_instruction")
    private ClientInstructionTypes clientInstruction;

    @Column(nullable = false)
    private DocumentStatusTypes status;

    public enum ClientInstructionTypes {
        INSPECTION_REPORT, FLASH_REPORT, INSPECTION_RELEASE_NOTE, NCR, PUNCHLISH, OTHERS
    }

    public enum DocumentStatusTypes {
        YES, NO, NA
    }

}

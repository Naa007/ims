package com.stepup.ims.entity;

import com.stepup.ims.audit.Auditable;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "contract_document")
public class ContractDocument extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentDescriptionTypes documentDescription;

    @Column(nullable = false)
    private DocumentStatusTypes status;

    @Column(name = "special_remarks", columnDefinition = "TEXT")
    private String specialRemarks;

    public enum DocumentDescriptionTypes {
        NOI,
        ITP,
        DATA_SHEET,
        PERFORMANCE_CURVE,
        GAD,
        SHOP_INSPECTION_TEST_PROCEDURE,
        FR_IR_NCR_IRN_PL,
        PROCEDURES_AS_PER_ITP,
        OTHER
    }

    public enum DocumentStatusTypes {
        YES,
        NO,
        NA
    }
}


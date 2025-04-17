package com.stepup.ims.entity;

import com.stepup.ims.audit.Auditable;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "scope_of_inspection")
public class ScopeOfInspection extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScopeOfInspectionTypes scopeOfInspection;

    @Column(nullable = false)
    private DocumentStatusTypes status;

    public enum ScopeOfInspectionTypes {
        DOCUMENT_REVIEW, SAMPLING_INSPECTION, STAGE_FINAL_INSPECTION, OTHERS
    }

    public enum DocumentStatusTypes {
        YES, NO, NA
    }
}

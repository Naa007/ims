package com.stepup.ims.entity;

import com.stepup.ims.audit.Auditable;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "inspection_advise")
public class InspectionAdvise extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inspection_advise_id", referencedColumnName = "id")
    private List<ScopeOfInspection> scopeOfInspectionList;

    @Column(name = "requirement")
    private String requirement;

    @Enumerated(EnumType.STRING)
    @Column(name = "reporting_format")
    private ReportingFormats reportingFormat;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inspection_advise_id", referencedColumnName = "id")
    private List<ClientInstructionToInspector> clientInstructionToInspectorList;

    @Column(name = "instructions_from_technical_team")
    private String instructionsFromTechnicalTeam;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inspection_advise_id", referencedColumnName = "id")
    private List<AcknowledgeFromInspector> acknowledgeFromInspectorList;

    @Column(name = "inspection_id", nullable = true)
    private Long inspectionId;

    public enum ReportingFormats {
        IISPL, CLIENT
    }
}

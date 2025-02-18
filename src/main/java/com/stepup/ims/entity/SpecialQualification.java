package com.stepup.ims.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "special_qualifications")
public class SpecialQualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inspector_id", nullable = false, referencedColumnName = "inspectorId")
    private Inspector assignedInspector;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private SpecialQualificationType type;

    @Column(nullable = true)
    private String specialQualificationDetails;

    public enum SpecialQualificationType {
        ARAMCO, SEC
    }
}

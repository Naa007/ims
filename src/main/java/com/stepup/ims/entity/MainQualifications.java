package com.stepup.ims.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "main_qualifications")
public class MainQualifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Qualifications for various fields (e.g., Mechanical Static, Rotating, etc.)
    @Column(name = "mech_static", nullable = true)
    private Boolean mechStatic;

    @Column(name = "mech_rotating", nullable = true)
    private Boolean mechRotating;

    @Column(nullable = true)
    private Boolean piping;

    @Column(name = "steel_structure", nullable = true)
    private Boolean steelStructure;

    @Column(nullable = true)
    private Boolean electrical;

    @OneToOne(fetch = FetchType.LAZY, optional = true) // Inspector is optional in this relationship
    @JoinColumn(name = "inspector_id", referencedColumnName = "inspector_id", nullable = true)
    private Inspector inspector;

}

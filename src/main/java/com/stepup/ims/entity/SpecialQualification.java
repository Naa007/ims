package com.stepup.ims.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "special_qualifications")
public class SpecialQualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "aramco", nullable = false)
    private Boolean aramco;

    @Column(name = "sec", nullable = false)
    private Boolean sec;

    @Column(nullable = true)
    private String specialQualificationDetails;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "inspector_id", referencedColumnName = "inspector_id", nullable = true)
    private Inspector inspector;

}

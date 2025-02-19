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

    @Column(name = "aramco", nullable = false)
    private Boolean aramco;

    @Column(name = "sec", nullable = false)
    private Boolean sec;

    @Column(nullable = true)
    private String specialQualificationDetails;

}

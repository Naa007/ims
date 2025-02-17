package com.stepup.ims.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // Primary Key

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = true)
    private Boolean eligibleForInspection;

    @OneToMany(mappedBy = "coordinator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inspection> inspectionsCoordinated = new HashSet<>(); // Coordinator's assigned inspections

    public enum Role {
        COORDINATOR, TECHNICAL_COORDINATOR, INSPECTOR, FREELANCER, PARTNER_INSPECTOR
    }
}
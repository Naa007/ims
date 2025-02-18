package com.stepup.ims.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Inspection_User")
public class InspectionUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key for Inspection_User entry

    @ManyToOne
    @JoinColumn(name = "inspection_id", referencedColumnName = "inspectionId")
    private Inspection inspection; // Relation to Inspection

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user; // Relation to User

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleInInspection roleInInspection;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusInInspection statusInInspection;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(nullable = false)
    private LocalDateTime assignmentDate;

    // Constructors, Getters, Setters...

    public enum RoleInInspection {
        TECHNICAL_COORDINATOR,
        INSPECTOR,
        FREELANCER,
        PARTNER_INSPECTOR
    }

    public enum StatusInInspection {
        ASSIGNED,
        IN_PROGRESS,
        COMPLETED,
        REJECTED
    }
}

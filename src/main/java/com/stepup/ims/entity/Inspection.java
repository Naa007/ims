package com.stepup.ims.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "Inspection")
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inspectionId; // Primary Key

    @Column(nullable = false, length = 255)
    private String inspectionName; // Title of the inspection

    @Column(columnDefinition = "TEXT")
    private String description; // Detailed description of the inspection

    @Column(nullable = false)
    private LocalDate inspectionDate; // Scheduled date for the inspection

    @ManyToOne
    @JoinColumn(name = "coordinator_id", referencedColumnName = "userId", nullable = true)
    private User coordinator; // Relation to User entity in the role of Coordinator

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "statusId", nullable = true)
    private Status status; // Relation to StatusTable to track lifecycle status

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InspectionUser> inspectionUsers = new HashSet<>(); // Relation to InspectionUser for participants

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Timestamp for creation

    // Constructors
    public Inspection() {
    }

    public Inspection(String inspectionName, String description, LocalDate inspectionDate, User coordinator, Status status) {
        this.inspectionName = inspectionName;
        this.description = description;
        this.inspectionDate = inspectionDate;
        this.coordinator = coordinator;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}

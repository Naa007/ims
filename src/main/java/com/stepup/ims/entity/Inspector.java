package com.stepup.ims.entity;

import com.google.maps.model.LatLng;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "Inspector")
public class Inspector {

    @Id
    @Column(name = "inspector_id", nullable = false)
    private Long inspectorId;

    @Column(nullable = false, length = 200)
    private String inspectorName;

    @Column(nullable = false, length = 100)
    private InspectorType inspectorType;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = true, length = 255)
    private String address;

    @Column(nullable = true, length = 100)
    private LatLng addressCoordinates;

    @Temporal(TemporalType.DATE) // To store the Date of Birth
    @DateTimeFormat(pattern = "dd/mm/yyyy")
    @Column(nullable = false)
    private Date dob;

    @Column(nullable = false, length = 500)
    private String educationDetails;

    @Column(nullable = true, length = 1000)
    private String disciplines;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inspector_id", referencedColumnName = "inspector_id")
    private List<Certificate> certificates;

    @OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
    @JoinColumn(name = "special_qualification_id", unique = false)
    private SpecialQualification specialQualification;

    @OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
    @JoinColumn(name = "main_qualification_id")
    private MainQualifications mainQualificationCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspector_status", nullable = false)
    private InspectorStatusType inspectorStatus;

    @OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
    @JoinColumn(name = "pqr_id")
    private PQR pqr;

    @Column(nullable = false) // Allow null for remarks
    private String remarks;

    public enum InspectorType {
        INHOUSE_INSPECTOR, TECHNICAL_COORDINATOR, FREELANCER, PARTNER_INSPECTOR
    }

    public enum InspectorStatusType {
        ACTIVE, INACTIVE, DELETED
    }
}

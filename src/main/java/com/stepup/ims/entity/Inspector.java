package com.stepup.ims.entity;

import com.google.maps.model.LatLng;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "Inspector")
public class Inspector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Primary Key
    private Long inspectorId;

    @Column(nullable = false, length = 200)
    private String inspectorName;

    @Column(nullable = false, length = 100)
    private InspectorType inspectorType;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 100)
    private LatLng addressCoordinates;

    @Temporal(TemporalType.DATE) // To store the Date of Birth
    @Column(nullable = false)
    private Date dob;

    @Column(nullable = false, length = 500)
    private String educationDetails;

    @Column(nullable = true, length = 1000)
    private String disciplines; // E.g., list or custom type separated by commas


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inspector_id", referencedColumnName = "inspectorId")
    private List<Certificate> relevantCertificates;


    @OneToOne
    @JoinColumn(name = "special_qualification_id", referencedColumnName = "id", nullable = true)
    private SpecialQualification specialQualification; // Special Qualification like Aramco, SEC, etc.


    @OneToOne
    @JoinColumn(name = "main_qualification_id", referencedColumnName = "id", nullable = false)
    private MainQualifications mainQualificationCategory;

    @Column(nullable = true) // Allow null for remarks
    private String remarks;

    public enum InspectorType{
        INHOUSE_INSPECTOR, TECHNICAL_COORDINATOR, FREELANCER, PARTNER_INSPECTOR
    }


}

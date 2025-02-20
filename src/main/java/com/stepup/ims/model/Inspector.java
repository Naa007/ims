package com.stepup.ims.model;

import com.google.maps.model.LatLng;
import lombok.Data;

import java.util.List;

@Data
public class Inspector {

    private Long inspectorId;
    private String inspectorName;
    private InspectorType inspectorType;
    private String phone;
    private String email;
    private String country;
    private String address;
    private LatLng addressCoordinates;
    private String dob; // Date for inspector's date of birth
    private String educationDetails;
    private String disciplines; // Comma-separated string for disciplines
    private List<Certificate> certificates;
    private SpecialQualification specialQualification;
    private MainQualification mainQualificationCategory;
    private InspectorStatusType inspectorStatus;
    private String remarks;

    public enum InspectorType {
        INHOUSE_INSPECTOR, TECHNICAL_COORDINATOR, FREELANCER, PARTNER_INSPECTOR
    }

    public enum InspectorStatusType {
        ACTIVE, INACTIVE, DELETED
    }
}

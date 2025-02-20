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

    // TODO replace with the google maps api call based on user location
    public LatLng getAddressCoordinates() {
        return new LatLng(17.4065,78.4772);
    }
    public enum InspectorType {
        INHOUSE_INSPECTOR, TECHNICAL_COORDINATOR, FREELANCER, PARTNER_INSPECTOR
    }

    public enum InspectorStatusType {
        ACTIVE, INACTIVE, DELETED
    }
}

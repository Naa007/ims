package com.stepup.ims.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.maps.model.LatLng;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
    @JsonManagedReference
    private List<Certificate> certificates= new ArrayList<>();
    private SpecialQualification specialQualification = new SpecialQualification();
    private MainQualification mainQualificationCategory = new MainQualification();
    private InspectorStatusType inspectorStatus;
    private String remarks;
    private PQR pqr;
    private String certificatesJson;

    public enum InspectorType {
        INHOUSE_INSPECTOR, TECHNICAL_COORDINATOR, FREELANCER, PARTNER_INSPECTOR
    }

    public enum InspectorStatusType {
        ACTIVE, INACTIVE, DELETED
    }

    public List<String> getCountriesList() {
        return Arrays.stream(Locale.getISOCountries())
                .map(countryCode -> new Locale("", countryCode).getDisplayCountry())
                .toList();
    }
}

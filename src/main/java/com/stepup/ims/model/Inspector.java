package com.stepup.ims.model;

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
    private List<Certificate> certificates= new ArrayList<>();
    private SpecialQualification specialQualification = new SpecialQualification();;
    private MainQualification mainQualificationCategory = new MainQualification();
    private InspectorStatusType inspectorStatus;
    private String remarks;
    private PQR pqr;

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

    public List<String> getCountriesList() {
        return Arrays.stream(Locale.getISOCountries())
                .map(countryCode -> new Locale("", countryCode).getDisplayCountry())
                .toList();
    }
}

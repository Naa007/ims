package com.stepup.ims.model;

import com.google.maps.model.LatLng;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate dob; // Date for inspector's date of birth
    private String educationDetails;
    private String disciplines; // Comma-separated string for disciplines
    private List<Certificate> certificates= new ArrayList<>();
    private SpecialQualification specialQualification = new SpecialQualification();
    private MainQualification mainQualificationCategory = new MainQualification();
    private InspectorStatusType inspectorStatus;
    private String remarks;
    private PQR pqr;

    public enum InspectorType {
        INHOUSE_INSPECTOR("Inhouse Inspector"),
        TECHNICAL_COORDINATOR("Technical Coordinator"),
        FREELANCER("Freelancer"),
        PARTNER_INSPECTOR("Partner Inspector");

        private final String description;

        InspectorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum InspectorStatusType {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        BLOCKED("BLOCKED");

        private final String description;

        InspectorStatusType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public List<String> getCountriesList() {
        return Arrays.stream(Locale.getISOCountries())
                .map(countryCode -> new Locale("", countryCode).getDisplayCountry())
                .toList();
    }
}

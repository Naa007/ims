package com.stepup.ims.model;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Data
public class Client {
    private String clientName;
    private String country;
    private String address;
    private String email;
    private String phone;
    private ReferenceType reference;
    private Long employeeId;
    private String confirmationDate;

    private List<String> countriesList = getCountriesList();

    public enum ReferenceType {
        EMAIL_CONFIRMATION, PHONE_CONFIRMATION
    }

    private List<String> getCountriesList(){
        return Arrays.stream(Locale.getISOCountries())
                .map(countryCode -> new Locale("", countryCode).getDisplayCountry())
                .toList();
    }

}

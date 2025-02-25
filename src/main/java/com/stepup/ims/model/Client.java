package com.stepup.ims.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Data
public class Client {
    private Long clientId;
    private String clientName;
    private String country;
    private String address;
    private String email;
    private String phone;
    private ReferenceType reference;
    private Employee employee;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate confirmationDate;

    private List<String> countriesList;

    public enum ReferenceType {
        EMAIL_CONFIRMATION, PHONE_CONFIRMATION
    }

    public List<String> getCountriesList() {
        return Arrays.stream(Locale.getISOCountries())
                .map(countryCode -> new Locale("", countryCode).getDisplayCountry())
                .toList();
    }

}

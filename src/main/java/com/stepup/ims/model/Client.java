package com.stepup.ims.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stepup.ims.utils.DataUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

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

    @JsonIgnore
    private List<String> countriesList;

    public List<String> getCountriesList() {
        return DataUtils.getCountriesList();
    }

    public enum ReferenceType {
        EMAIL_CONFIRMATION, PHONE_CONFIRMATION
    }

}

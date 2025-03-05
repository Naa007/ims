package com.stepup.ims.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
public class Certificate {

    private Long id;
    private String name;
    private LocalDate dateIssued;
    private LocalDate expiryDate;
    private String issuer;
    @JsonBackReference
    @ToString.Exclude
    private Inspector inspector;
}

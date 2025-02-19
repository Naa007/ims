package com.stepup.ims.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Certificate {

    private String name;
    private LocalDate dateIssued;
    private LocalDate expiryDate;
    private String issuer;
}

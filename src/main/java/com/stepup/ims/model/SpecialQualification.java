package com.stepup.ims.model;

import lombok.Data;
import lombok.ToString;

@Data
public class SpecialQualification {
    private Long id;
    private Boolean aramco;
    private Boolean sec;
    private String specialQualificationDetails;
    @ToString.Exclude
    private Inspector inspector;
}

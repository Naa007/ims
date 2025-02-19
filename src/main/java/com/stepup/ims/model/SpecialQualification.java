package com.stepup.ims.model;

import lombok.Data;

@Data
public class SpecialQualification {

    private Inspector assignedInspector;
    private Boolean aramco;
    private Boolean sec;
    private String specialQualificationDetails;
}

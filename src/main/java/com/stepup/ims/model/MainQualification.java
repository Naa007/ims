package com.stepup.ims.model;

import lombok.Data;
import lombok.ToString;

@Data
public class MainQualification {
    private Long id;
    private Boolean mechStatic;
    private Boolean mechRotating;
    private Boolean piping;
    private Boolean steelStructure;
    private Boolean electrical;
    @ToString.Exclude
    private Inspector inspector;
}

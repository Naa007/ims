package com.stepup.ims.model;

import lombok.Data;

@Data
public class MainQualification {

    private Long id;
    private Boolean mechStatic;
    private Boolean mechRotating;
    private Boolean piping;
    private Boolean steelStructure;
    private Boolean electrical;
}

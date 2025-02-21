package com.stepup.ims.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class Employee {

    @NotNull
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String empName;
    @NotBlank
    @Size(max = 30)
    private String empId;
    @NotNull
    private String role;
    @NotNull
    @Pattern(regexp = "true|false", message = "Value must be either 'true' or 'false'")
    private String active;
    @NotBlank
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

}

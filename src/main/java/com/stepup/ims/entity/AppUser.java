package com.stepup.ims.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table
public class AppUser {

    @Id
    private String email; // Email serving as the unique identifier
    private String phoneNumber; // Mobile Number
    private boolean isVerified; // Store if OTP is verified
}

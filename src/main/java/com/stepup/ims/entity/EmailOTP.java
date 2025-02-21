package com.stepup.ims.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table
public class EmailOTP {

    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String otpCode;
    private LocalDateTime expiryTime; // OTP expiration time

    // Getters and Setters
}
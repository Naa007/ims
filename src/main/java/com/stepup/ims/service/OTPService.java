package com.stepup.ims.service;

import com.stepup.ims.entity.EmailOTP;
import com.stepup.ims.repository.EmailOTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private EmailOTPRepository otpRepository;

    public String generateOTP(String email) {
        Random random = new Random();
        String otp = String.valueOf(100000 + random.nextInt(900000)); // Generate 6-digit OTP

        // Store OTP in the database
        EmailOTP otpEntity = new EmailOTP();
        otpEntity.setEmail(email);
        otpEntity.setOtpCode(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5)); // OTP valid for 5 minutes
        otpRepository.save(otpEntity);

        return otp;
    }

    public boolean validateOTP(String email, String otpCode) {
        EmailOTP otpEntity = otpRepository.findByEmailAndOtpCode(email, otpCode);

        if (otpEntity == null) {
            return false; // OTP doesn't exist
        }

        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            return false; // OTP expired
        }

        // OTP is valid, delete it to avoid reuse
        otpRepository.delete(otpEntity);
        return true;
    }
}
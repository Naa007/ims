package com.stepup.ims.repository;

import com.stepup.ims.entity.EmailOTP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailOTPRepository extends JpaRepository<EmailOTP, Long> {
    EmailOTP findByEmailAndOtpCode(String email, String otpCode);
}

package com.stepup.ims.controller;

import com.stepup.ims.model.AppUser;
import com.stepup.ims.service.AppUserService;
import com.stepup.ims.service.EmailOTPService;
import com.stepup.ims.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailOTPService emailService;

    @Autowired
    private AppUserService appUserService;

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam String email) {
        String otp = otpService.generateOTP(email);

        // Send OTP via email
        try {
            emailService.sendEmail(email, "Your OTP Code", "Your OTP is: " + otp);
        } catch (Exception e) {
            return "Failed to send OTP: " + e.getMessage();
        }

        return "OTP sent to your email!";
    }

    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam String email, @RequestParam String otpCode) {
        if (otpService.validateOTP(email, otpCode)) {
            // Mark user as authenticated
            AppUser user = appUserService.findByEmail(email).orElse(new AppUser());
            user.setEmail(email);
            user.setVerified(true);
            appUserService.save(user);

            // Automatically log in the user
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            return "Login successful";
        } else {
            return "Invalid OTP or OTP expired!";
        }
    }
}
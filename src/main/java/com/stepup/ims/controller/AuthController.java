package com.stepup.ims.controller;

import com.stepup.ims.model.AppUser;
import com.stepup.ims.service.AppUserService;
import com.stepup.ims.service.EmailOTPService;
import com.stepup.ims.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailOTPService emailService;

    @Autowired
    private AppUserService appUserService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/send-otp")
    @ResponseBody
    public String sendOTP(@RequestParam String email) {
        // Send OTP via email
        try {
            String otp = otpService.generateOTP(email);
            emailService.sendEmail(email, "Your OTP Code", "Your OTP is: " + otp);
        } catch (RuntimeException re) {
            return re.getMessage();
        } catch (Exception e) {
            return "Failed to send OTP: " + e.getMessage();
        }

        return "OTP sent to your email!";
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otpCode) {
        if (otpService.validateOTP(email, otpCode)) {
            // Mark user as authenticated
            AppUser user = appUserService.findByEmail(email).orElse(new AppUser());
            user.setEmail(email);
            user.setVerified(true);
            appUserService.save(user);

            // Automatically log in the user
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/admin/dashboard").build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or OTP expired!");
        }
    }
}
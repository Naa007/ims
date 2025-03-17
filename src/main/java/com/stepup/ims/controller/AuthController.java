package com.stepup.ims.controller;

import com.stepup.ims.model.AppUser;
import com.stepup.ims.service.AppUserService;
import com.stepup.ims.service.EmailOTPService;
import com.stepup.ims.service.EmployeeService;
import com.stepup.ims.service.OTPService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.stepup.ims.constants.UIRoutingConstants.*;
import static com.stepup.ims.constants.ApplicationConstants.*;


@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailOTPService emailService;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        return "redirect:/auth/login?logout=true";
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
    public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otpCode, HttpSession session) {
        if (otpService.validateOTP(email, otpCode)) {
            // Mark user as authenticated
            AppUser user = appUserService.findByEmail(email).orElse(new AppUser());
            user.setEmail(email);
            user.setVerified(true);
            user.setRole(employeeService.getRoleByEmail(email));
            appUserService.save(user);

            // Automatically log in the user
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            String employeeName = employeeService.getEmployeeNameByEmail(email);
            session.setAttribute("employeeName", employeeName);

            String redirectUrl;
            switch (user.getRole().toUpperCase()) {
                case ADMIN:
                    redirectUrl = ADMIN_DASHBOARD_URL;
                    break;
                case COORDINATOR:
                    redirectUrl = COORDINATOR_DASHBOARD_URL;
                    break;
                case TECHNICAL_COORDINATOR:
                    redirectUrl = TECHNICAL_COORDINATOR_DASHBOARD_URL;
                    break;
                case BUSINESS:
                    redirectUrl = BUSINESS_DASHBOARD_URL;
                    break;
                case INSPECTOR:
                    redirectUrl = INSPECTOR_DASHBOARD_URL;
                    break;
                default:
                    redirectUrl = DEFAULT_DASHBOARD_URL;
                    break;
            }
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrl).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or OTP expired!");
        }
    }
}
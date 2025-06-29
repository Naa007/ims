package com.stepup.ims.service;

import com.stepup.ims.entity.EmailOTP;
import com.stepup.ims.model.Employee;
import com.stepup.ims.repository.EmailOTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class OTPService {

    private static final Logger logger = LoggerFactory.getLogger(OTPService.class);

    @Autowired
    private EmailOTPRepository otpRepository;

    @Autowired
    private EmployeeService employeeService;

    public String generateOTP(String email) {
        logger.info("Generating OTP for email: {}", email);
        List<Employee> employees = employeeService.getAllEmployees();

        if(employees.isEmpty()) {
            logger.error("No employees found in the system");
            throw new RuntimeException("No employees found, please contact your administrator");
        }
        if (!verifyEmployee(employeeService.getAllEmployees(), email)) {
            logger.warn("Email not registered or inactive: {}", email);
            throw new RuntimeException("Your email is NOT registered in system, please contact your administrator");
        }

        Random random = new Random();
        String otp = String.valueOf(100000 + random.nextInt(900000));
        logger.debug("Generated 6-digit OTP");

        // Store OTP in the database
        EmailOTP otpEntity = new EmailOTP();
        otpEntity.setEmail(email);
        otpEntity.setOtpCode(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5)); // OTP valid for 5 minutes
        otpRepository.save(otpEntity);
        logger.info("OTP saved for email: {}", email);
        return otp;
       
    }

    public boolean validateOTP(String email, String otpCode) {
        logger.info("Validating OTP for email: {}", email);
        EmailOTP otpEntity = otpRepository.findByEmailAndOtpCode(email, otpCode);

        if (otpEntity == null) {
            logger.warn("OTP not found for email: {} and code: {}", email, otpCode);
            return false;
        }

        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            logger.warn("OTP expired for email: {}", email);
            return false;
        }

        // OTP is valid, delete it to avoid reuse
        otpRepository.delete(otpEntity);
        logger.info("OTP validated and deleted for email: {}", email);
        return true;
    }
    
    private boolean verifyEmployee(List<Employee> allEmployees, String email) {
        return allEmployees.stream()
                .anyMatch(employee -> email.equalsIgnoreCase(employee.getEmail()) && "yes".equalsIgnoreCase(employee.getActive()));
    }
}
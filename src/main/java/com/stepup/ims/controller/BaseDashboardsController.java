package com.stepup.ims.controller;

import com.stepup.ims.model.Employee;
import com.stepup.ims.model.InspectionStatsByRole;
import com.stepup.ims.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

public abstract class BaseDashboardsController {

    private static final Logger logger = LoggerFactory.getLogger(BaseDashboardsController.class);

    @Autowired
    protected EmployeeService employeeService;

    protected String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Retrieved current user email");
        return (authentication != null) ? authentication.getName() : null;
    }

    protected Employee getCurrentEmployee(String email) {
        return (email != null) ? employeeService.getEmployeeByEmail(email) : null;
    }

    protected void populateCommonDashboardAttributes(Model model, Employee employee, String email, InspectionStatsByRole statsData) {
        logger.info("Populating dashboard attributes for user: {}", email);
        if (email != null) {
            logger.debug("Added userEmail to model: {}", email);
            model.addAttribute("userEmail", email);
        }
        if (employee != null) {
            model.addAttribute("employeeName", employee.getEmpName());
            model.addAttribute("employeeRole", employee.getRole());
            model.addAttribute("employeeId", employee.getEmpId());
            logger.debug("Added employee info to model: {} ({})", employee.getEmpName(), employee.getRole());
        }
        model.addAttribute("inspectionStats", statsData);
        logger.debug("Added inspectionStats to model for email: {}", email);
    }
}

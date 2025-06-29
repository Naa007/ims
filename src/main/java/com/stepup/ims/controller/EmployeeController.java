package com.stepup.ims.controller;

import com.stepup.ims.model.Employee;
import com.stepup.ims.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.stepup.ims.constants.UIRoutingConstants.REDIRECT_ADMIN_EMPLOYEE_MANAGEMENT;
import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_EMPLOYEE_MANAGEMENT;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);


    @Autowired
    private EmployeeService employeeService;

    // Employee Management Page
    @GetMapping("/employee-management")
    public String showEmployeeManagement(Model model) {
        logger.info("Accessing Employee Management page.");
        logger.debug("Fetching all employees for management view.");

        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("employee", new Employee());
        logger.info("Employee management data loaded.");
        return RETURN_TO_EMPLOYEE_MANAGEMENT;
    }

    // Save Employee
    @PostMapping("/save-employee")
    public String saveEmployee(@ModelAttribute Employee employee, RedirectAttributes redirectAttributes) {
        logger.info("Attempting to save new employee.");
        logger.debug("Validating employee before save (new).");
        try {
            employeeService.validateEmployee(employee.getEmpId(),employee.getEmail(), "new");
            employeeService.saveEmployee(employee);
            logger.info("Employee saved successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Validation failed while saving employee: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return REDIRECT_ADMIN_EMPLOYEE_MANAGEMENT;
    }

    // Save Employee
    @PostMapping("/update-employee")
    public String updateEmployee(@ModelAttribute Employee employee, RedirectAttributes redirectAttributes) {
        logger.info("Attempting to update existing employee.");
        logger.debug("Validating employee before update.");

        try {
            employeeService.validateEmployee(employee.getEmpId(),employee.getEmail(), "update");
            employeeService.saveEmployee(employee);
            logger.info("Employee updated successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Validation failed while updating employee: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return REDIRECT_ADMIN_EMPLOYEE_MANAGEMENT;
    }
}

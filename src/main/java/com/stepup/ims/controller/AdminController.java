package com.stepup.ims.controller;

import com.stepup.ims.model.Employee;
import com.stepup.ims.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.stepup.ims.constants.UIRountingConstants.REDIRECT_ADMIN_EMPLOYEE_MANAGEMENT;
import static com.stepup.ims.constants.UIRountingConstants.RETURN_TO_EMPLOYEE_MANAGEMENT;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EmployeeService employeeService;

    // Dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        return "dashboard";
    }

    // Employee Management Page
    @GetMapping("/employee-management")
    public String showEmployeeManagement(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("employee", new Employee()); // Add empty employee object for the form
        return RETURN_TO_EMPLOYEE_MANAGEMENT;
    }

    // Save Employee
    @PostMapping("/save-employee")
    public String saveEmployee(@ModelAttribute Employee employee) {
        employeeService.saveEmployee(employee);
        return REDIRECT_ADMIN_EMPLOYEE_MANAGEMENT;
    }

}
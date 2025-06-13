package com.stepup.ims.controller;

import com.stepup.ims.model.Employee;
import com.stepup.ims.service.EmployeeService;
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


    @Autowired
    private EmployeeService employeeService;

    // Employee Management Page
    @GetMapping("/employee-management")
    public String showEmployeeManagement(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("employee", new Employee()); // Add empty employee object for the form
        return RETURN_TO_EMPLOYEE_MANAGEMENT;
    }

    // Save Employee
    @PostMapping("/save-employee")
    public String saveEmployee(@ModelAttribute Employee employee, RedirectAttributes redirectAttributes) {
        try {
            employeeService.validateEmployee(employee.getEmpId(),employee.getEmail(), "new");
            employeeService.saveEmployee(employee);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return REDIRECT_ADMIN_EMPLOYEE_MANAGEMENT;
    }

    // Save Employee
    @PostMapping("/update-employee")
    public String updateEmployee(@ModelAttribute Employee employee, RedirectAttributes redirectAttributes) {
        try {
            employeeService.validateEmployee(employee.getEmpId(),employee.getEmail(), "update");
            employeeService.saveEmployee(employee);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return REDIRECT_ADMIN_EMPLOYEE_MANAGEMENT;
    }
}

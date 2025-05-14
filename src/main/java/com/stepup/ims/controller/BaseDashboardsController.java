package com.stepup.ims.controller;

import com.stepup.ims.model.Employee;
import com.stepup.ims.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

public abstract class BaseDashboardsController {

    @Autowired
    protected EmployeeService employeeService;

    protected String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    protected Employee getCurrentEmployee(String email) {
        return employeeService.getEmployeeByEmail(email);
    }

    protected void populateCommonDashboardAttributes(Model model, Employee employee, String email, String statsKey, Object statsData) {
        model.addAttribute("userEmail", email);
        model.addAttribute("employeeName", employee.getEmpName());
        model.addAttribute("employeeRole", employee.getRole());
        model.addAttribute(statsKey, statsData);
    }
}

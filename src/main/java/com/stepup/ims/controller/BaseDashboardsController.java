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
        return (authentication != null) ? authentication.getName() : null;
    }

    protected Employee getCurrentEmployee(String email) {
        return (email != null) ? employeeService.getEmployeeByEmail(email) : null;
    }

    protected void populateCommonDashboardAttributes(Model model, Employee employee, String email, String statsKey, Object statsData) {
        if (email != null) {
            model.addAttribute("userEmail", email);
        }
        if (employee != null) {
            model.addAttribute("employeeName", employee.getEmpName());
            model.addAttribute("employeeRole", employee.getRole());
            model.addAttribute("employeeId", employee.getEmpId());
        }
        model.addAttribute(statsKey, statsData);
    }
}

package com.stepup.ims.controller;

import com.stepup.ims.entity.Employee;
import com.stepup.ims.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        return "employee-management";
    }

    // Save Employee
    @PostMapping("/save-employee")
    public String saveEmployee(@ModelAttribute Employee employee) {
        employeeService.saveEmployee(employee);
        return "redirect:/admin/employee-management";
    }


    @PostMapping("/update-employee/{id}")
    public String updateEmployee(@PathVariable Long id, @ModelAttribute Employee employee) {
        System.out.println("Updating Employee ID: " + id);
        System.out.println("Updated Data: " + employee);
        employee.setId(id);
        employeeService.saveEmployee(employee);
        return "redirect:/admin/employee-management";
    }

    @GetMapping("/delete-employee/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/admin/employee-management";
    }
}
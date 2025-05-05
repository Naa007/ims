package com.stepup.ims.controller;

import com.stepup.ims.entity.Inspection;
import com.stepup.ims.model.Employee;
import com.stepup.ims.model.InpsectionStatsByRole;
import com.stepup.ims.repository.InspectionRepository;
import com.stepup.ims.service.EmployeeService;
import com.stepup.ims.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_COORDINATOR_DASHBOARD;

@Controller
@RequestMapping("/coordinator")
@PreAuthorize("hasRole('COORDINATOR')")
public class CoordinatorRoleController {
    @Autowired
    private  EmployeeService employeeService;
    @Autowired
    private InspectionRepository inspectionRepository;
    @Autowired
    private StatsService statsService;


    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // Get the employee details
        Employee currentEmployee = employeeService.getEmployeeByEmail(currentUserEmail);

        // Get coordinator-specific stats
        Map<String, Object> coordinatorStats = statsService.getInspections(currentUserEmail);

        // Add attributes to the model
        model.addAttribute("userEmail", currentUserEmail);
        model.addAttribute("employeeName", currentEmployee.getEmpName());
        model.addAttribute("employeeRole", currentEmployee.getRole());
        model.addAttribute("coordinatorStats", coordinatorStats);

        return RETURN_TO_COORDINATOR_DASHBOARD;
    }
}

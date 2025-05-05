package com.stepup.ims.controller;

import com.stepup.ims.model.Employee;
import com.stepup.ims.model.Inspection;
import com.stepup.ims.repository.InspectionRepository;
import com.stepup.ims.service.EmployeeService;
import com.stepup.ims.service.InspectionService;
import com.stepup.ims.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.stepup.ims.constants.UIRoutingConstants.*;

@Controller
@RequestMapping("/inspector")
@PreAuthorize("hasRole('INSPECTOR')")
public class InspectorRoleController {

    @Autowired
    private InspectionService inspectionService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private InspectionRepository inspectionRepository;
    @Autowired
    private StatsService statsService;

    @GetMapping("/inspection-management")
    public String getInspectionsReviewedByLoggedUser(Model model) {
        model.addAttribute("inspections", inspectionService.getInspectionsOfInspectorByLoggedUser());
        return RETURN_TO_INSPECTOR_INSPECTION_MANAGEMENT;
    }

    @GetMapping("inspection/view/{inspectionId}")
    public String viewInspection(@PathVariable String inspectionId, Model model) {
        Inspection inspection = inspectionService.getInspectionById(Long.valueOf(inspectionId)).orElseThrow(() -> new IllegalArgumentException("Inspection not found for ID:" + inspectionId));
        model.addAttribute("inspection", inspection);
        return RETURN_TO_INSPECTOR_INSPECTION_VIEW;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // Get the employee details
        Employee currentEmployee = employeeService.getEmployeeByEmail(currentUserEmail);

        // Get coordinator-specific stats
        Map<String, Object> inspectorStats = statsService.getInspections(currentUserEmail);
        model.addAttribute("userEmail", currentUserEmail);
        model.addAttribute("employeeName", currentEmployee.getEmpName());
        model.addAttribute("employeeRole", currentEmployee.getRole());
        model.addAttribute("inspectorStats", inspectorStats);
        return RETURN_TO_INSPECTOR_DASHBOARD;
    }
}

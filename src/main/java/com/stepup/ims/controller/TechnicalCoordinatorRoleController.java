package com.stepup.ims.controller;

import com.stepup.ims.model.Employee;
import com.stepup.ims.model.InspectionStatsByRole;
import com.stepup.ims.model.Inspection;
import com.stepup.ims.service.InspectionService;
import com.stepup.ims.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.stepup.ims.constants.ApplicationConstants.TOTAL;
import static com.stepup.ims.constants.UIRoutingConstants.*;

@Controller
@RequestMapping("/technical-coordinator")
@PreAuthorize("hasRole('TECHNICAL_COORDINATOR')")
public class TechnicalCoordinatorRoleController extends BaseDashboardsController {

    @Autowired
    private InspectionService inspectionService;
    @Autowired
    private StatsService statsService;

    @GetMapping("/inspection-management")
    public String getInspectionsReviewedByLoggedUser(Model model) {
        model.addAttribute("inspections", inspectionService.getInspectionsReviewedByLoggedUser());
        return RETURN_TO_TECHNICAL_COORDINATOR_INSPECTION_MANAGEMENT;
    }

    @GetMapping("inspection/edit/{inspectionId}")
    public String showInspectionForm(@PathVariable String inspectionId, Model model) {
        Inspection inspection = inspectionService.getInspectionById(Long.valueOf(inspectionId)).orElseThrow(() -> new IllegalArgumentException("Inspection not found for ID: " + inspectionId));

        model.addAttribute("inspection", inspection);
        return RETURN_TO_TECHNICAL_COORDINATOR_INSPECTION_FORM;
    }

    @PostMapping(value = "inspection/save")
    public String updateInspection(@ModelAttribute Inspection inspection) {
        inspectionService.saveInspection(inspection);
        return REDIRECT_TO_TECHNICAL_COORDINATOR_INSPECTION_MANAGEMENT;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        String email = getCurrentUserEmail();
        Employee employee = getCurrentEmployee(email);
        LocalDate startDate = LocalDate.now().withDayOfMonth(1); // Start of current month
        LocalDate endDate = LocalDate.now(); // Today
        // Get coordinator-specific stats
        InspectionStatsByRole stats = statsService.getTechnicalCoordinatorStats(employee.getEmpId(), TOTAL, startDate, endDate);
        populateCommonDashboardAttributes(model, employee, email, stats);
        return RETURN_TO_TECHNICAL_COORDINATOR_DASHBOARD;
    }

}

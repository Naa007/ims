package com.stepup.ims.controller;

import com.stepup.ims.model.Employee;
import com.stepup.ims.model.Inspection;
import com.stepup.ims.model.InspectionStatsByRole;
import com.stepup.ims.service.InspectionService;
import com.stepup.ims.service.StatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

import static com.stepup.ims.constants.ApplicationConstants.TOTAL;
import static com.stepup.ims.constants.UIRoutingConstants.*;

@Controller
@RequestMapping("/inspector")
@PreAuthorize("hasRole('INSPECTOR')")
public class InspectorRoleController extends BaseDashboardsController {

    private static final Logger logger = LoggerFactory.getLogger(InspectorRoleController.class);

    @Autowired
    private InspectionService inspectionService;
    @Autowired
    private StatsService statsService;

    @GetMapping("/inspection-management")
    public String getInspectionsReviewedByLoggedUser(Model model) {
        logger.info("Fetching inspections assigned to the logged-in inspector.");
        model.addAttribute("inspections", inspectionService.getInspectionsOfInspectorByLoggedUser());
        return RETURN_TO_INSPECTOR_INSPECTION_MANAGEMENT;
    }

    @GetMapping("inspection/view/{inspectionId}")
    public String viewInspection(@PathVariable String inspectionId, Model model) {
        logger.info("Viewing inspection with ID: {}", inspectionId);
        Inspection inspection = inspectionService.getInspectionById(Long.valueOf(inspectionId)).orElseThrow(() -> new IllegalArgumentException("Inspection not found for ID:" + inspectionId));
        model.addAttribute("inspection", inspection);
        return RETURN_TO_INSPECTOR_INSPECTION_VIEW;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        logger.info("Loading inspector dashboard.");
        String email = getCurrentUserEmail();
        logger.debug("Fetching employee by email: {}", email);
        Employee employee = getCurrentEmployee(email);
        logger.debug("Calculating date range for stats.");
        LocalDateTime endDate = LocalDateTime.now().withHour(00).withMinute(00).withSecond(00).withNano(000000001);
        LocalDateTime startDate = endDate.minusMonths(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        // Get coordinator-specific stats for one month
        logger.debug("Fetching inspection stats for inspector.");
        InspectionStatsByRole stats = statsService.getInspectorStats(email, TOTAL,startDate,endDate);
        populateCommonDashboardAttributes(model, employee, email, stats);
        logger.info("Inspector dashboard loaded successfully.");
        return RETURN_TO_INSPECTOR_DASHBOARD;
    }
}

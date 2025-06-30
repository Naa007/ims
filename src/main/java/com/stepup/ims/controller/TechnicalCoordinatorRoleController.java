package com.stepup.ims.controller;

import com.stepup.ims.model.Employee;
import com.stepup.ims.model.Inspection;
import com.stepup.ims.model.InspectionStatsByRole;
import com.stepup.ims.service.InspectionService;
import com.stepup.ims.service.InspectionReportsService;
import com.stepup.ims.service.StatsService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.stepup.ims.constants.ApplicationConstants.TOTAL;
import static com.stepup.ims.constants.UIRoutingConstants.*;

@Controller
@RequestMapping("/technical-coordinator")
@PreAuthorize("hasRole('TECHNICAL_COORDINATOR')")
public class TechnicalCoordinatorRoleController extends BaseDashboardsController {

    private static final Logger logger = LoggerFactory.getLogger(TechnicalCoordinatorRoleController.class);

    @Autowired
    private InspectionService inspectionService;
    @Autowired
    private StatsService statsService;

    @Autowired
    private InspectionReportsService inspectionReportsService;

    @GetMapping("/inspection-management")
    public String getInspectionsReviewedByLoggedUser(Model model) {
        logger.info("Technical Coordinator accessing inspection management page.");
        model.addAttribute("inspections", inspectionService.getInspectionsReviewedByLoggedUser());
        return RETURN_TO_TECHNICAL_COORDINATOR_INSPECTION_MANAGEMENT;
    }

    @GetMapping("inspection/edit/{inspectionId}")
    public String showInspectionForm(@PathVariable String inspectionId, Model model) {
        logger.info("Editing inspection with ID: {}", inspectionId);
        Inspection inspection = inspectionService.getInspectionById(Long.valueOf(inspectionId)).orElseThrow(() -> new IllegalArgumentException("Inspection not found for ID: " + inspectionId));

        model.addAttribute("inspection", inspection);
        return RETURN_TO_TECHNICAL_COORDINATOR_INSPECTION_FORM;
    }

    @PostMapping(value = "inspection/save")
    public String updateInspection(@ModelAttribute Inspection inspection) {
        logger.info("Saving inspection with ID");
        inspectionService.saveInspection(inspection);
        return REDIRECT_TO_TECHNICAL_COORDINATOR_INSPECTION_MANAGEMENT;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        logger.info("Technical Coordinator accessing dashboard.");
        String email = getCurrentUserEmail();
        logger.debug("Fetching employee by email: {}", email);
        Employee employee = getCurrentEmployee(email);
        LocalDateTime endDate = LocalDateTime.now().withHour(00).withMinute(00).withSecond(00).withNano(000000001);
        LocalDateTime startDate = endDate.minusMonths(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        // Get coordinator-specific stats for one month
        logger.debug("Getting stats for empId: {} from {} to {}", employee.getEmpId(), startDate, endDate);
        InspectionStatsByRole stats = statsService.getTechnicalCoordinatorStats(employee.getEmpId(), TOTAL, startDate, endDate);
        populateCommonDashboardAttributes(model, employee, email, stats);
        return RETURN_TO_TECHNICAL_COORDINATOR_DASHBOARD;
    }
    @PostMapping(value = "/inspectionReports/updateComments", consumes = "application/json")
    @ResponseBody
    public String updateInspectionReportComments(@RequestBody CommentRequestDto request) {
        logger.info("Starting update of inspection report comments for ID: {}", request.getId());
        String result = inspectionReportsService.updateTechnicalCoordinatorRemarksById(request.getId(), request.getRemarks());
        logger.info("InspectionReportsService.updateReportComments() called successfully for ID: {}", request.getId());
        logger.info("Result: {}", result);
        return result;
    }

    // DTO to map JSON data
    @Data
    public static class CommentRequestDto {
        private Long id;
        private String remarks;
    }


}

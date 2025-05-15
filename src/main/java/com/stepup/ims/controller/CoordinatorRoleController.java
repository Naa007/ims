package com.stepup.ims.controller;

import com.stepup.ims.model.Employee;
import com.stepup.ims.model.InspectionStatsByRole;
import com.stepup.ims.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.stepup.ims.constants.ApplicationConstants.TOTAL;
import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_COORDINATOR_DASHBOARD;

@Controller
@RequestMapping("/coordinator")
@PreAuthorize("hasRole('COORDINATOR')")
public class CoordinatorRoleController extends BaseDashboardsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        String email = getCurrentUserEmail();
        Employee employee = getCurrentEmployee(email);
        // Get coordinator-specific stats
        InspectionStatsByRole stats = statsService.getCoordinatorStats(email, TOTAL);
        populateCommonDashboardAttributes(model, employee, email, stats);
        return RETURN_TO_COORDINATOR_DASHBOARD;
    }
}

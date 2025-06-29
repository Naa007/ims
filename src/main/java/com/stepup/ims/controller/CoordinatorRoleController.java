package com.stepup.ims.controller;

import com.stepup.ims.model.Employee;
import com.stepup.ims.model.InspectionStatsByRole;
import com.stepup.ims.service.ClientService;
import com.stepup.ims.service.StatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

import static com.stepup.ims.constants.ApplicationConstants.TOTAL;
import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_COORDINATOR_DASHBOARD;

@Controller
@RequestMapping("/coordinator")
@PreAuthorize("hasRole('COORDINATOR')")
public class CoordinatorRoleController extends BaseDashboardsController {

    private static final Logger logger = LoggerFactory.getLogger(CoordinatorRoleController.class);

    @Autowired
    private StatsService statsService;

    @Autowired
    private ClientService clientService;
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        logger.info("Coordinator dashboard accessed.");
        String email = getCurrentUserEmail();
        logger.debug("Fetching current employee by email.");
        Employee employee = getCurrentEmployee(email);

        logger.debug("Calculating start and end date for one-month stats window.");
        LocalDateTime endDate = LocalDateTime.now().withHour(00).withMinute(00).withSecond(00).withNano(000000001);
        LocalDateTime startDate = endDate.minusMonths(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        logger.debug("Retrieving coordinator stats from statsService.");
        InspectionStatsByRole stats = statsService.getCoordinatorStats(email, TOTAL, startDate, endDate);
        logger.debug("Populating model with common dashboard attributes.");
        populateCommonDashboardAttributes(model, employee, email, stats);

        logger.debug("Fetching client list for coordinator view.");
        model.addAttribute("clients", clientService.getAllClients());

        return RETURN_TO_COORDINATOR_DASHBOARD;
    }
}

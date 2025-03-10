package com.stepup.ims.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_TECHNICAL_COORDINATOR_DASHBOARD;

@Controller
@RequestMapping("/technical-coordinator")
@PreAuthorize("hasRole('TECHNICAL_COORDINATOR')")
public class TechnicalCoordinatorRoleController {

    @GetMapping("/dashboard")
    public String showDashboard() {
        return RETURN_TO_TECHNICAL_COORDINATOR_DASHBOARD;
    }

}

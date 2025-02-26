package com.stepup.ims.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/technical-coordinator")
@PreAuthorize("hasRole('TECHNICAL_COORDINATOR')")
public class TechnicalCoordinatorRoleController {

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "technical-coordinator-dashboard";
    }

}

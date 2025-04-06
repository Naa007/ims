package com.stepup.ims.controller;

import com.stepup.ims.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_BUSINESS_DASHBOARD;

@Controller
@RequestMapping("/business")
@PreAuthorize("hasRole('BUSINESS')")
public class BusinessRoleController {

    @Autowired
    StatsService statsService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> businessStats = statsService.getBusinessStats();
        // Add the statistics to the model
        model.addAttribute("employeeStats", businessStats.get("Employee Stats"));
        model.addAttribute("clientStats", businessStats.get("Client Stats"));
        model.addAttribute("inspectorStats", businessStats.get("Inspector Stats"));
        model.addAttribute("inspectionStats", businessStats.get("Inspection Stats"));
        return RETURN_TO_BUSINESS_DASHBOARD;
    }
}

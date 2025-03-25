package com.stepup.ims.controller;

import com.stepup.ims.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_BUSINESS_DASHBOARD;

@Controller
@RequestMapping("/business")
@PreAuthorize("hasRole('BUSINESS')")
public class BusinessRoleController {

    @Autowired
    ReportsService reportsService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("employeeStats", reportsService.getEmployeeStatistics());
        model.addAttribute("clientStats", reportsService.getClientStatistics());
        model.addAttribute("inspectorStats", reportsService.getInspectorStatistics());
        model.addAttribute("inspectionStats", reportsService.getInspectionStatistics());
        return RETURN_TO_BUSINESS_DASHBOARD;
    }
}

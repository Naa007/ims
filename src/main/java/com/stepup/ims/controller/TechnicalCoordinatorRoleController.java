package com.stepup.ims.controller;

import com.stepup.ims.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_TECHNICAL_COORDINATOR_DASHBOARD;
import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_TECHNICAL_COORDINATOR_REVIEWED;

@Controller
@RequestMapping("/technical-coordinator")
@PreAuthorize("hasRole('TECHNICAL_COORDINATOR')")
public class TechnicalCoordinatorRoleController {

    @Autowired
    private InspectionService inspectionService;


    @GetMapping("/inspections-reviewed")
    public String getInspectionsReviewedByLoggedUser(Model model) {
        model.addAttribute("inspections", inspectionService.getInspectionsReviewedByLoggedUser());
        return RETURN_TO_TECHNICAL_COORDINATOR_REVIEWED;
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return RETURN_TO_TECHNICAL_COORDINATOR_DASHBOARD;
    }

}

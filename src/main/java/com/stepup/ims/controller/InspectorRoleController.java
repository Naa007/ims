package com.stepup.ims.controller;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.stepup.ims.constants.UIRoutingConstants.*;

@Controller
@RequestMapping("/inspector")
@PreAuthorize("hasRole('INSPECTOR')")
public class InspectorRoleController {

    @Autowired
    private InspectionService inspectionService;

    @GetMapping("/inspection-management")
    public String getInspectionsReviewedByLoggedUser(Model model) {
        model.addAttribute("inspections", inspectionService.getInspectionsOfInspectorByLoggedUser());
        return RETURN_TO_INSPECTOR_INSPECTION_MANAGEMENT;
    }

    @GetMapping("inspection/view/{inspectionId}")
    public String viewInspection(@PathVariable String inspectionId, Model model) {
        Inspection inspection = inspectionService.getInspectionById(Long.valueOf(inspectionId)).orElseThrow(() -> new IllegalArgumentException("Inspection not found for ID:" + inspectionId));
        model.addAttribute("inspection", inspection);
        return RETURN_TO_INSPECTOR_INSPECTION_VIEW;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        return RETURN_TO_INSPECTOR_DASHBOARD;
    }
}

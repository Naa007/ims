package com.stepup.ims.controller;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.model.ProposedCVs;
import com.stepup.ims.modelmapper.InspectionModelMapper;
import com.stepup.ims.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.stepup.ims.constants.ApplicationConstants.INSPECTION;
import static com.stepup.ims.constants.ApplicationConstants.INSPECTIONS;
import static com.stepup.ims.constants.UIRountingConstants.*;

@Controller
@RequestMapping("/inspection")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private InspectorService inspectorService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private GoogleMapsService googleMapsService;

    @Autowired
    private InspectionModelMapper inspectionModelMapper;

    @GetMapping("/new")
    public String showInspectorForm(Model model) {
        Inspection inspection = new Inspection();

        // set clients for dropdown
        inspection.setClientsList(clientService.getAllClients());

        // set inspectors for dropdown
        inspection.setInspectorsList(inspectorService.getAllActiveInspectors());

        // set technical coordinators for dropdown
        inspection.setTechnicalCoordinatorsList(employeeService.getAllTechnicalCoordinateEmployees());

        ProposedCVs proposedCVs = new ProposedCVs();

        inspection.setProposedCVs(List.of(proposedCVs));

        model.addAttribute(INSPECTION, inspection);
        model.addAttribute("edit", false);
        return RETURN_TO_INSPECTION_NEW;
    }


    @PostMapping(value = "/save")
    public String createInspection(@ModelAttribute Inspection inspection) {
        inspectionService.saveInspection(inspection);
        return REDIRECT_INSPECTION_MANAGEMENT;
    }

    @GetMapping(value = "/edit/{inspectionId}")
    public String editInspection(@PathVariable String inspectionId, Model model) {

        Inspection inspection = inspectionService.getInspectionById(Long.valueOf(inspectionId))
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found for ID: " + inspectionId));

        inspection.setClientsList(clientService.getAllClients());

        // set inspectors for dropdown
        inspection.setInspectorsList(inspectorService.getAllActiveInspectors());

        // set technical coordinators for dropdown
        inspection.setTechnicalCoordinatorsList(employeeService.getAllTechnicalCoordinateEmployees());

        model.addAttribute(INSPECTION, inspection);
        model.addAttribute("edit", true);
        return RETURN_TO_INSPECTION_NEW;

    }

    @GetMapping(value = "/view/{inspectionId}")
    public String viewInspection(@PathVariable String inspectionId, Model model) {
        Inspection inspection = inspectionService.getInspectionById(Long.valueOf(inspectionId))
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found for ID: " + inspectionId));

        model.addAttribute(INSPECTION, inspection);
        return RETURN_TO_INSPECTION_VIEW;
    }


    @GetMapping("/inspection-management")
    public String getAllInspections(Model model) {
        List<Inspection> inspections = inspectionService.getAllInspections();
        model.addAttribute(INSPECTIONS, inspections);
        return RETURN_TO_INSPECTION_MANAGEMENT;
    }

}

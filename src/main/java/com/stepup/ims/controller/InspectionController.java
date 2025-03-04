package com.stepup.ims.controller;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.model.ProposedCVs;
import com.stepup.ims.modelmapper.InspectionModelMapper;
import com.stepup.ims.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/inspection")
@PreAuthorize( "hasRole('ROLE_COORDINATOR')")
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

    @GetMapping("/form")
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

        model.addAttribute("inspection", inspection);
        return "inspection-management";
    }

    @PostMapping(value = "/save")
    public String createInspection(Inspection inspection) {
        inspectionService.saveInspection(inspection);
        return "redirect:/inspection/form";
    }

    @GetMapping
    public List<Inspection> getAllInspections() {
        return inspectionService.getAllInspections();
    }

}

package com.stepup.ims.controller;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.model.ProposedCVs;
import com.stepup.ims.modelmapper.InspectionModelMapper;
import com.stepup.ims.service.ClientService;
import com.stepup.ims.service.InspectionService;
import com.stepup.ims.service.InspectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
    private InspectionModelMapper inspectionModelMapper;

    @GetMapping("/form")
    public String showInspectorForm(Model model) {
        Inspection inspection = new Inspection();

        // set clients for dropdown
        inspection.setClientsList(clientService.getAllClients());

        // set inspectors for dropdown
        inspection.setInspectorsList(inspectorService.getAllInspectors());

        // set technical coordinators for dropdown
        inspection.setTechnicalCoordinatorsList(inspectorService.getAllTechnicalCoordinators());

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

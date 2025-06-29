package com.stepup.ims.controller;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.model.InspectionReports;
import com.stepup.ims.model.ProposedCVs;
import com.stepup.ims.modelmapper.InspectionModelMapper;
import com.stepup.ims.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.stepup.ims.constants.ApplicationConstants.INSPECTION;
import static com.stepup.ims.constants.ApplicationConstants.INSPECTIONS;
import static com.stepup.ims.constants.UIRoutingConstants.*;

@Controller
@RequestMapping("/inspection")
public class InspectionController {

    private static final Logger logger = LoggerFactory.getLogger(InspectionController.class);

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

    @Autowired
    private EmailService emailService;

    @GetMapping("/new")
    public String showInspectorForm(Model model) {
        logger.info("Accessing new inspection form.");
        Inspection inspection = new Inspection();

        // set clients for dropdown
        inspection.setClientsList(clientService.getAllClients());

        // set inspectors for dropdown
        inspection.setInspectorsList(inspectorService.getAllActiveInspectors());

        // set technical coordinators for dropdown
        inspection.setTechnicalCoordinatorsList(employeeService.getAllTechnicalCoordinateEmployees());

        ProposedCVs proposedCVs = new ProposedCVs();
        inspection.setProposedCVs(List.of(proposedCVs));

        InspectionReports inspectionReports = new InspectionReports();
        inspection.setInspectionReports(List.of(inspectionReports));


        model.addAttribute(INSPECTION, inspection);
        model.addAttribute("edit", false);
        logger.debug("New inspection form prepared with dropdown data.");
        return RETURN_TO_INSPECTION_FORM;
    }


    @PostMapping(value = "/save")
    public String createInspection(@ModelAttribute Inspection inspection) {
        logger.info("Saving inspection (create or update).");
        Inspection originalInspection = inspection.getId() == null ? inspection :
                inspectionService.getInspectionById(inspection.getId()).orElse(null);
        Inspection savedInspection = inspectionService.saveInspection(inspection);
        logger.debug("Inspection saved with ID: {}", savedInspection.getId());
        emailService.sendNotificationEmail(originalInspection, savedInspection);
        logger.info("Notification email triggered for inspection ID: {}", savedInspection.getId());
        return REDIRECT_INSPECTION_MANAGEMENT;
    }

    @GetMapping(value = "/edit/{inspectionId}")
    public String editInspection(@PathVariable String inspectionId, Model model) {
        logger.info("Editing inspection with ID: {}", inspectionId);
        Inspection inspection = inspectionService.getInspectionById(Long.valueOf(inspectionId))
                .orElseThrow(() -> {
                    logger.error("Inspection not found for ID: {}", inspectionId);
                    return new IllegalArgumentException("Inspection not found for ID: " + inspectionId);
                });

        if (inspection.getProposedCVs() == null || inspection.getProposedCVs().isEmpty()) {
            inspection.setProposedCVs(List.of(new ProposedCVs()));
        }

        if (inspection.getInspectionReports() == null || inspection.getInspectionReports().isEmpty()) {
            inspection.setInspectionReports(List.of(new InspectionReports()));
        }
        
        inspection.setClientsList(clientService.getAllClients());

        // set inspectors for dropdown
        inspection.setInspectorsList(inspectorService.getAllActiveInspectors());

        // set technical coordinators for dropdown
        inspection.setTechnicalCoordinatorsList(employeeService.getAllTechnicalCoordinateEmployees());

        model.addAttribute(INSPECTION, inspection);
        model.addAttribute("edit", true);
        logger.info("Inspection edit form loaded successfully for ID: {}", inspectionId);
        return RETURN_TO_INSPECTION_FORM;

    }

    @GetMapping(value = "/view/{inspectionId}")
    public String viewInspection(@PathVariable String inspectionId, Model model) {
        logger.info("Viewing inspection with ID: {}", inspectionId);
        Inspection inspection = inspectionService.getInspectionById(Long.valueOf(inspectionId))
                .orElseThrow(() -> {
                    logger.error("Inspection not found for ID to view: {}", inspectionId);
                    return new IllegalArgumentException("Inspection not found for ID: " + inspectionId);
                });

        model.addAttribute(INSPECTION, inspection);
        return RETURN_TO_INSPECTION_VIEW;
    }


    @GetMapping("/inspection-management")
    public String getAllInspections(Model model) {
        logger.info("Fetching all inspections created by the current user.");
        List<Inspection> inspections = inspectionService.getAllInspectionsByCreatedBy();
        logger.debug("Total inspections retrieved: {}", inspections.size());
        model.addAttribute(INSPECTIONS, inspections);
        return RETURN_TO_INSPECTION_MANAGEMENT;
    }

}

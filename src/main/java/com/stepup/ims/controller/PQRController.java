package com.stepup.ims.controller;

import com.stepup.ims.model.Inspector;
import com.stepup.ims.model.PQR;
import com.stepup.ims.service.EmailService;
import com.stepup.ims.service.PQRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.stepup.ims.constants.ApplicationConstants.*;
import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_PQR_FORM;
import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_PQR_VIEW;

@Controller
@RequestMapping("/pqr")
public class PQRController {

    private static final Logger logger = LoggerFactory.getLogger(PQRController.class);

    @Autowired
    PQRService pqrService;

    @Autowired
    EmailService emailService;

    @GetMapping("/edit/{inspectorId}/{inspectionId}")
    public String editPQRForm(Model model, @PathVariable Long inspectorId, @PathVariable Long inspectionId) {
        logger.info("Accessing PQR edit form.");
        logger.debug("Inspector ID: {}, Inspection ID: {}", inspectorId, inspectionId);
        Inspector inspector = pqrService.getPQRByInspectorId(inspectorId);
        model.addAttribute(INSPECTOR_LOWERCASE, inspector);
        model.addAttribute(PQR, inspector.getPqr());
        model.addAttribute("inspectionId", inspectionId);
        return RETURN_TO_PQR_FORM;
    }

    @GetMapping("/view/{inspectorId}")
    public String viewPQRForm(Model model, @PathVariable Long inspectorId) {
        logger.info("Viewing PQR form.");
        logger.debug("Inspector ID: {}", inspectorId);

        Inspector inspector = pqrService.getPQRByInspectorId(inspectorId);
        model.addAttribute(INSPECTOR_LOWERCASE, inspector);
        model.addAttribute(PQR, inspector.getPqr());
        return RETURN_TO_PQR_VIEW;
    }

    @PostMapping("/update/{action}/{inspectionId}")
    public String updateInspectorPRQ(@ModelAttribute PQR pqr, Model model, @PathVariable String action, @PathVariable Long inspectionId) {
        logger.info("Updating PQR for inspector.");
        Inspector inspector = pqrService.updateInspectorPQR(pqr.getInspectorId(), pqr);
        emailService.sendPQRNotification(inspector, action, inspectionId);
        model.addAttribute(INSPECTOR_LOWERCASE, inspector);
        model.addAttribute(PQR, inspector.getPqr());
        model.addAttribute(SUCCESS_MESSAGE, "PQR updated successfully!");
        logger.info("PQR updated and notification sent.");
        return RETURN_TO_PQR_FORM;
    }
}

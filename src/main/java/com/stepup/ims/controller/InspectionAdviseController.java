package com.stepup.ims.controller;

import com.stepup.ims.model.InspectionAdvise;
import com.stepup.ims.model.Inspection;
import com.stepup.ims.service.InspectionAdviseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.stepup.ims.constants.ApplicationConstants.*;
import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_INSPECTION_ADVISE_FORM;
import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_INSPECTION_ADVISE_VIEW;

@Controller
@RequestMapping("/inspectionAdvise")
public class InspectionAdviseController {

    private static final Logger logger = LoggerFactory.getLogger(InspectionAdviseController.class);

    @Autowired
    InspectionAdviseService inspectionAdviseService;

    @GetMapping("/edit/{inspectionId}")
    public String editInspectionAdviseForm(Model model, @PathVariable Long inspectionId) {
        logger.info("Accessing inspection advise edit form for inspectionId: {}", inspectionId);
        logger.debug("Fetching inspection with advise details.");
        Inspection inspection = inspectionAdviseService.getInspectionAdviseByInspectionId(inspectionId);
        model.addAttribute(INSPECTION, inspection);
        model.addAttribute(INSPECTION_ADVISE, inspection.getInspectionAdvise());
        logger.info("Inspection advise form loaded for editing.");
        return RETURN_TO_INSPECTION_ADVISE_FORM;
    }

    @GetMapping("/view/{inspectionId}")
    public String viewInspectionAdviseForm(Model model, @PathVariable Long inspectionId) {
        logger.info("Viewing inspection advise for inspectionId: {}", inspectionId);
        Inspection inspection = inspectionAdviseService.getInspectionAdviseByInspectionId(inspectionId);
        model.addAttribute(INSPECTION, inspection);
        model.addAttribute(INSPECTION_ADVISE, inspection.getInspectionAdvise());
        logger.info("Inspection advise view loaded.");
        return RETURN_TO_INSPECTION_ADVISE_VIEW;
    }


    @PostMapping("/update")
    public String updateInspectionAdvise(@ModelAttribute InspectionAdvise inspectionAdvise, Model model) {
        logger.info("Updating inspection advise for inspectionId: {}", inspectionAdvise.getInspectionId());
        logger.debug("Calling service to update inspection advise.");
        Inspection inspection = inspectionAdviseService.updateInspectionContactReview(inspectionAdvise.getInspectionId(), inspectionAdvise);
        model.addAttribute(INSPECTION, inspection);
        model.addAttribute(INSPECTION_ADVISE, inspection.getInspectionAdvise());
        model.addAttribute(SUCCESS_MESSAGE, "Inspection Advise Document was updated successfully!");
        logger.info("Inspection advise updated successfully for inspectionId: {}", inspectionAdvise.getInspectionId());
        return RETURN_TO_INSPECTION_ADVISE_FORM;
    }
}

package com.stepup.ims.controller;

import com.stepup.ims.model.InspectionAdvise;
import com.stepup.ims.model.Inspection;
import com.stepup.ims.service.InspectionAdviseService;
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

    @Autowired
    InspectionAdviseService inspectionAdviseService;

    @GetMapping("/edit/{inspectionId}")
    public String editInspectionAdviseForm(Model model, @PathVariable Long inspectionId) {
        Inspection inspection = inspectionAdviseService.getInspectionAdviseByInspectionId(inspectionId);
        model.addAttribute(INSPECTION, inspection);
        model.addAttribute(INSPECTION_ADVISE, inspection.getInspectionAdvise());
        return RETURN_TO_INSPECTION_ADVISE_FORM;
    }

    @GetMapping("/view/{inspectionId}")
    public String viewInspectionAdviseForm(Model model, @PathVariable Long inspectionId) {
        Inspection inspection = inspectionAdviseService.getInspectionAdviseByInspectionId(inspectionId);
        model.addAttribute(INSPECTION, inspection);
        model.addAttribute(INSPECTION_ADVISE, inspection.getInspectionAdvise());
        return RETURN_TO_INSPECTION_ADVISE_VIEW;
    }


    @PostMapping("/update")
    public String updateInspectionAdvise(@ModelAttribute InspectionAdvise inspectionAdvise, Model model) {
        Inspection inspection = inspectionAdviseService.updateInspectionContactReview(inspectionAdvise.getInspectionId(), inspectionAdvise);
        model.addAttribute(INSPECTION, inspection);
        model.addAttribute(INSPECTION_ADVISE, inspection.getInspectionAdvise());
        model.addAttribute(SUCCESS_MESSAGE, "Inspection Advise Document was updated successfully!");
        return RETURN_TO_INSPECTION_ADVISE_FORM;
    }
}

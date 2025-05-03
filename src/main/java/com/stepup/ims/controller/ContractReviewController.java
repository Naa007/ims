package com.stepup.ims.controller;

import com.stepup.ims.model.ContractReview;
import com.stepup.ims.model.Inspection;
import com.stepup.ims.service.ContractReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.stepup.ims.constants.ApplicationConstants.*;
import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_CONTRACT_REVIEW_FORM;
import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_CONTRACT_REVIEW_VIEW;

@Controller
@RequestMapping("/contractReview")
public class ContractReviewController {

    @Autowired
    ContractReviewService contractReviewService;

    @GetMapping("/edit/{inspectionId}")
    public String editContractReviewForm(Model model, @PathVariable Long inspectionId) {
        Inspection inspection = contractReviewService.getContractReviewByInspectionId(inspectionId);
        model.addAttribute(INSPECTION, inspection);
        model.addAttribute(CONTRACT_REVIEW, inspection.getContractReview());
        return RETURN_TO_CONTRACT_REVIEW_FORM;
    }

    @GetMapping("/view/{inspectionId}")
    public String viewContractReviewForm(Model model, @PathVariable Long inspectionId) {
        Inspection inspection = contractReviewService.getContractReviewByInspectionId(inspectionId);
        model.addAttribute(INSPECTION, inspection);
        model.addAttribute(CONTRACT_REVIEW, inspection.getContractReview());
        return RETURN_TO_CONTRACT_REVIEW_VIEW;
    }

    @PostMapping("/update")
    public String updateInspectionContractReview(@ModelAttribute ContractReview contractReview, Model model) {
        Inspection inspection = contractReviewService.updateInspectionContactReview(contractReview.getInspectionId(), contractReview);
        model.addAttribute(INSPECTION, inspection);
        model.addAttribute(CONTRACT_REVIEW, inspection.getContractReview());
        model.addAttribute(SUCCESS_MESSAGE, "Contract Review Document was updated successfully!");
        return RETURN_TO_CONTRACT_REVIEW_FORM;
    }
}

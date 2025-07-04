package com.stepup.ims.controller;

import com.stepup.ims.model.ContractReview;
import com.stepup.ims.model.Inspection;
import com.stepup.ims.service.ContractReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ContractReviewController.class);

    @Autowired
    ContractReviewService contractReviewService;

    @GetMapping("/edit/{inspectionId}")
    public String editContractReviewForm(Model model, @PathVariable Long inspectionId) {
        logger.info("Editing contract review form for inspectionId: {}", inspectionId);
        logger.debug("Fetching inspection and contract review details.");
        Inspection inspection = contractReviewService.getContractReviewByInspectionId(inspectionId);
        model.addAttribute(INSPECTION, inspection);
        model.addAttribute(CONTRACT_REVIEW, inspection.getContractReview());

        logger.info("Contract review form loaded successfully for editing. InspectionId: {}", inspectionId);
        return RETURN_TO_CONTRACT_REVIEW_FORM;
    }

    @GetMapping("/view/{inspectionId}")
    public String viewContractReviewForm(Model model, @PathVariable Long inspectionId) {
        logger.info("Viewing contract review for inspectionId: {}", inspectionId);
        Inspection inspection = contractReviewService.getContractReviewByInspectionId(inspectionId);
        model.addAttribute(INSPECTION, inspection);
        model.addAttribute(CONTRACT_REVIEW, inspection.getContractReview());

        logger.info("Contract review view loaded successfully. InspectionId: {}", inspectionId);
        return RETURN_TO_CONTRACT_REVIEW_VIEW;
    }

    @PostMapping("/update")
    public String updateInspectionContractReview(@ModelAttribute ContractReview contractReview, Model model) {
        logger.info("Updating contract review for inspectionId: {}", contractReview.getInspectionId());
        logger.debug("Calling service to update contract review.");

        Inspection inspection = contractReviewService.updateInspectionContactReview(contractReview.getInspectionId(), contractReview);
        model.addAttribute(INSPECTION, inspection);
        model.addAttribute(CONTRACT_REVIEW, inspection.getContractReview());
        model.addAttribute(SUCCESS_MESSAGE, "Contract Review Document was updated successfully!");
        return RETURN_TO_CONTRACT_REVIEW_FORM;
    }
}

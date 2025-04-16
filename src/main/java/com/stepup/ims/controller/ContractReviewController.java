package com.stepup.ims.controller;

import com.stepup.ims.model.ContractReview;
import com.stepup.ims.model.Inspection;
import com.stepup.ims.service.ContractReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_CONTRACT_REVIEW_FORM;

@Controller
@RequestMapping("/contractReview")
public class ContractReviewController {

    @Autowired
    ContractReviewService contractReviewService;

    @GetMapping("/edit/{inspectorId}")
    public String editContractReviewForm(Model model, @PathVariable Long inspectorId) {
        Inspection inspection = contractReviewService.getContractReviewByInspectionId(inspectorId);
        model.addAttribute("inspection", inspection);
        model.addAttribute("contractReview", inspection.getContractReview());
        return RETURN_TO_CONTRACT_REVIEW_FORM;
    }

    @PostMapping("/update")
    public String updateInspectionContractReview(@ModelAttribute ContractReview contractReview, Model model) {
        Inspection inspection = contractReviewService.updateInspectionContactReview(contractReview.getInspectionId(), contractReview);
        model.addAttribute("inspection", inspection);
        model.addAttribute("contractReview", inspection.getContractReview());
        model.addAttribute("successMessage", "Contract Review Document was updated successfully!");
        return RETURN_TO_CONTRACT_REVIEW_FORM;
    }
}

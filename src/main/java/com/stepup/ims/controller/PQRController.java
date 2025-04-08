package com.stepup.ims.controller;

import com.stepup.ims.model.Inspector;
import com.stepup.ims.model.PQR;
import com.stepup.ims.service.PQRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_PQR_FORM;

@Controller
@RequestMapping("/pqr")
public class PQRController {

    @Autowired
    PQRService pqrService;

    @GetMapping("/edit/{inspectorId}")
    public String editPQRForm(Model model, @PathVariable Long inspectorId) {
        Inspector inspector = pqrService.getPQRByInspectorId(inspectorId);
        model.addAttribute("inspector", inspector);
        model.addAttribute("pqr", inspector.getPqr());
        return RETURN_TO_PQR_FORM;
    }

    @PostMapping("/update")
    public String updateInspectoPRQ(@ModelAttribute PQR pqr, Model model) {
        Inspector inspector = pqrService.updateInspectorPQR(pqr.getInspectorId(), pqr);
        model.addAttribute("inspector", inspector);
        model.addAttribute("pqr", inspector.getPqr());
        model.addAttribute("successMessage", "PQR updated successfully!");
        return RETURN_TO_PQR_FORM;
    }
}

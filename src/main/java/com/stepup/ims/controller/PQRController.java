package com.stepup.ims.controller;

import com.stepup.ims.model.Inspector;
import com.stepup.ims.service.PQRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_PQR_FORM;
import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_PQR_VIEW;

@Controller
@RequestMapping("/pqr")
public class PQRController {

    @Autowired
    PQRService pqrService;

    @GetMapping("/edit/{inspectorId}")
    public String editPQRForm(Model model, @PathVariable Long inspectorId) {
        Inspector inspector = pqrService.getPQRByInspectorId(inspectorId);
        model.addAttribute("inspector", inspector);
        return RETURN_TO_PQR_FORM;
    }

    @GetMapping("/view/{inspectorId}")
    public String viewPQRForm(Model model, @PathVariable Long inspectorId) {
        Inspector inspector = pqrService.getPQRByInspectorId(inspectorId);
        model.addAttribute("inspector", inspector);
        return RETURN_TO_PQR_VIEW;
    }


    @PostMapping("/save")
    public String savePQR(Inspector inspector) {
        pqrService.savePQR(inspector);
        return RETURN_TO_PQR_VIEW;
    }
}

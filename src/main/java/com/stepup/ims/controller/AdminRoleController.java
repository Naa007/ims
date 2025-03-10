package com.stepup.ims.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_ADMIN_DASHBOARD;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoleController {

    // Dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        return RETURN_TO_ADMIN_DASHBOARD;
    }

}
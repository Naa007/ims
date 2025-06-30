package com.stepup.ims.controller;

import com.stepup.ims.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger logger = LoggerFactory.getLogger(AdminRoleController.class);

    @Autowired
    private ClientService clientService;

    // Dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        logger.info("Retrieved clients for admin dashboard");
        // Get clients list and add to the model
        model.addAttribute("clients", clientService.getAllClients());

        return RETURN_TO_ADMIN_DASHBOARD;
    }

}
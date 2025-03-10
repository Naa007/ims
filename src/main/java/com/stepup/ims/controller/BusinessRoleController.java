package com.stepup.ims.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_BUSINESS_DASHBOARD;

@Controller
@RequestMapping("/business")
@PreAuthorize("hasRole('BUSINESS')")
public class BusinessRoleController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return RETURN_TO_BUSINESS_DASHBOARD;
    }
}

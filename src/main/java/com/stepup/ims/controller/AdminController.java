package com.stepup.ims.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // Dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        return "dashboard";
    }

}
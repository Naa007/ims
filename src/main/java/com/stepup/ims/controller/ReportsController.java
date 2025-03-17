package com.stepup.ims.controller;

import com.stepup.ims.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/business")
public class ReportsController {

    @Autowired
    ReportsService reportsService;

    @GetMapping("/stats")
    public String getBusinessStats(Model model) {
        model.addAttribute("businessStats", reportsService.getBusinessStats());
        return "businessStats"; // Return the name of the UI page to display the stats
    }

}

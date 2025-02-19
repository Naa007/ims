package com.stepup.ims.controller;

import com.stepup.ims.model.Inspector;
import com.stepup.ims.service.InspectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/inspector")
public class InspectorController {

    @Autowired
    private InspectorService inspectorService;

    /**
     * Display list of all inspectors.
     */
    @GetMapping("/list")
    public String listInspectors(Model model) {
        List<Inspector> inspectors = inspectorService.getAllInspectors();

        model.addAttribute("inspectors", inspectors);
        return "inspectorList";
    }

    /**
     * Show the form to add a new inspector.
     */
    @GetMapping("/form")
    public String showInspectorForm(Model model) {
        model.addAttribute("inspector", new Inspector());
        return "InspectorForm";
    }

    /**
     * Save or update the inspector.
     */
    @PostMapping("/save")
    public String saveInspector(@ModelAttribute Inspector inspector) {
        inspectorService.saveInspector(inspector);
        return "redirect:/inspector/list";
    }

    /**
     * Show the form to edit an existing inspector.
     */
    @GetMapping("/edit/{id}")
    public String editInspector(@PathVariable("id") Long id, Model model) {
        inspectorService.getInspectorById(id).ifPresent(inspector -> model.addAttribute("inspector", inspector));
        return "InspectorForm";
    }

    /**
     * Delete an inspector.
     */
    @GetMapping("/delete/{id}")
    public String deleteInspector(@PathVariable("id") Long id) {
        inspectorService.deleteInspector(id);
        return "redirect:/inspector/list";
    }
}
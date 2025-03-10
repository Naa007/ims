package com.stepup.ims.controller;

import com.google.maps.model.LatLng;
import com.stepup.ims.model.Inspector;
import com.stepup.ims.service.GoogleMapsService;
import com.stepup.ims.service.InspectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.stepup.ims.constants.UIRoutingConstants.*;

@Controller
@RequestMapping("/inspectors")
public class InspectorController {

    @Autowired
    private InspectorService inspectorService;

    @Autowired
    private GoogleMapsService googleMapsService;

    /**
     * Display list of all inspectors.
     */
    @GetMapping("/list")
    public String listInspectors(Model model) {
        List<Inspector> inspectors = inspectorService.getAllInspectors();

        // Add the list of inspectors to the model
        model.addAttribute("inspectors", inspectors);

        // Add a new Inspector object to the model for the form
        model.addAttribute("inspector", new Inspector());

        return RETURN_TO_INSPECTOR_MANAGEMENT;
    }

    /**
     * Save or update the inspector.
     * Handles exception when geocoding the address.
     */
    @PostMapping("/save")
    public String saveInspector(Inspector inspector) {
        if (inspector.getAddress() != null && !inspector.getAddress().isEmpty()) {
            try {
                LatLng coordinates = googleMapsService.geocodeAddress(inspector.getAddress());
                inspector.setAddressCoordinates(coordinates);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                inspector.setAddressCoordinates(null);
            } catch (Exception e) {
                inspector.setAddressCoordinates(null);
            }
        }
        inspectorService.saveInspector(inspector);
        return REDIRECT_TO_INSPECTOR_MANAGEMENT;
    }

    /**
     * Fetch inspector details for editing .
     */
    @GetMapping("/edit/{id}")
    public String editInspector(@PathVariable Long id, Model model) {
        Inspector inspector = inspectorService.getInspectorById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inspector not found for ID: " + id));

        model.addAttribute("inspector", inspector);
        model.addAttribute("edit", true);
        return REDIRECT_TO_INSPECTOR_FORM; // Return the name of the Thymeleaf template for the inspector form
    }

    /**
     * Fetch inspector details for viewing .
     */
    @GetMapping("/view/{id}")
    public String viewInspector(@PathVariable Long id, Model model) {
        Inspector inspector = inspectorService.getInspectorById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inspector not found for ID: " + id));

        model.addAttribute("inspector", inspector);
        return REDIRECT_TO_INSPECTOR_VIEW; // Thymeleaf template name
    }

    /**
     * Show the form to add a new inspector.
     */
    @GetMapping("/form")
    public String showInspectorForm(Model model) {
        model.addAttribute("inspector", new Inspector());
        return REDIRECT_TO_INSPECTOR_FORM;
    }
}
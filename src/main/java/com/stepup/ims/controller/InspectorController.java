package com.stepup.ims.controller;

import com.google.maps.model.LatLng;
import com.stepup.ims.model.Inspector;
import com.stepup.ims.service.GoogleMapsService;
import com.stepup.ims.service.InspectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

        return "inspector-management";
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
                System.err.println("Geocoding interrupted: " + ie.getMessage());
                inspector.setAddressCoordinates(null);
            } catch (Exception e) {
                inspector.setAddressCoordinates(null);
                // Log the exception (assumes a logger is available)
                System.err.println("Cannot geocode address: " + e.getMessage());
            }
        }
        inspectorService.saveInspector(inspector);
        return "redirect:/inspectors/list";
    }

}
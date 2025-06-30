package com.stepup.ims.controller;

import com.google.maps.model.LatLng;
import com.stepup.ims.model.Inspector;
import com.stepup.ims.service.GoogleMapsService;
import com.stepup.ims.service.InspectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.stepup.ims.constants.ApplicationConstants.INSPECTOR_LOWERCASE;
import static com.stepup.ims.constants.UIRoutingConstants.*;

@Controller
@RequestMapping("/inspectors")
public class InspectorController {

    private static final Logger logger = LoggerFactory.getLogger(InspectorController.class);

    @Autowired
    private InspectorService inspectorService;

    @Autowired
    private GoogleMapsService googleMapsService;

    /**
     * Display list of all inspectors.
     */
    @GetMapping("/list")
    public String listInspectors(Model model) {
        logger.info("Fetching list of all inspectors.");
        List<Inspector> inspectors = inspectorService.getAllInspectors();

        // Add the list of inspectors to the model
        model.addAttribute("inspectors", inspectors);

        // Add a new Inspector object to the model for the form
        model.addAttribute(INSPECTOR_LOWERCASE, new Inspector());
        logger.debug("Total inspectors loaded: {}", inspectors.size());
        return RETURN_TO_INSPECTOR_MANAGEMENT;
    }

    /**
     * Save or update the inspector.
     * Handles exception when geocoding the address.
     */
    @PostMapping("/save")
    public String saveInspector(Inspector inspector, RedirectAttributes redirectAttributes) {
        logger.info("Saving inspector data.");
        if (inspector.getAddress() != null && !inspector.getAddress().isEmpty()) {
            try {
                logger.debug("Attempting to geocode inspector address.");
                LatLng coordinates = googleMapsService.geocodeAddress(inspector.getAddress());
                inspector.setAddressCoordinates(coordinates);
                logger.debug("Geocoding successful.");
            } catch (InterruptedException ie) {
                logger.warn("Geocoding interrupted, setting coordinates to null.");
                Thread.currentThread().interrupt();
                inspector.setAddressCoordinates(null);
            } catch (Exception e) {
                logger.error("Error occurred during geocoding: {}", e.getMessage());
                inspector.setAddressCoordinates(null);
            }
        }

        try {
            inspectorService.saveInspector(inspector);
            logger.info("Inspector saved successfully.");
        } catch (Exception e) {
            if (e.getCause().getMessage().contains("Duplicate entry")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Inspector with the email already present");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Something went wrong, contact Admin");
            }
        }
        
        return REDIRECT_TO_INSPECTOR_MANAGEMENT;
    }

    /**
     * Fetch inspector details for editing .
     */
    @GetMapping("/edit/{id}")
    public String editInspector(@PathVariable Long id, Model model) {
        logger.info("Editing inspector with ID: {}", id);
        Inspector inspector = inspectorService.getInspectorById(id).orElseThrow(() -> new IllegalArgumentException("Inspector not found for ID: " + id));

        model.addAttribute(INSPECTOR_LOWERCASE, inspector);
        model.addAttribute("edit", true);
        logger.debug("Inspector data loaded for editing.");
        return REDIRECT_TO_INSPECTOR_FORM; // Return the name of the Thymeleaf template for the inspector form
    }

    /**
     * Fetch inspector details for viewing .
     */
    @GetMapping("/view/{id}")
    public String viewInspector(@PathVariable Long id, Model model) {
        logger.info("Viewing inspector with ID: {}", id);
        Inspector inspector = inspectorService.getInspectorById(id).orElseThrow(() -> new IllegalArgumentException("Inspector not found for ID: " + id));

        model.addAttribute(INSPECTOR_LOWERCASE, inspector);
        logger.debug("Inspector data loaded for viewing.");
        return REDIRECT_TO_INSPECTOR_VIEW; // Thymeleaf template name
    }

    /**
     * Show the form to add a new inspector.
     */
    @GetMapping("/form")
    public String showInspectorForm(Model model) {
        logger.info("Accessing new inspector form.");
        model.addAttribute(INSPECTOR_LOWERCASE, new Inspector());
        return REDIRECT_TO_INSPECTOR_FORM;
    }
}
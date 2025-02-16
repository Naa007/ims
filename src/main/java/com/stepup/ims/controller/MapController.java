package com.stepup.ims.controller;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.stepup.ims.service.GoogleMapsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MapController {

    @Autowired
    private GoogleMapsService googleMapsService;

    // Mock: Replace with real inspectors' locations from database
    private final List<LatLng> inspectorLocations = List.of(
            new LatLng(12.9715987, 77.5945627), // Inspector 1: Bangalore
            new LatLng(28.7040592, 77.1024902), // Inspector 2: Delhi
            new LatLng(19.0760, 72.8777)        // Inspector 3: Mumbai
    );

    @GetMapping("/inspectors")
    public List<GoogleMapsService.InspectorDistance> getInspectorsByAddress(Model model, @RequestParam String address) throws InterruptedException, ApiException, IOException {
        // 1. Geocode the user's input address
        LatLng userLocation = googleMapsService.geocodeAddress(address);

        // 2. Calculate distances from user location to all inspectors
        return googleMapsService.getInspectorDistances(userLocation, inspectorLocations);
    }
}

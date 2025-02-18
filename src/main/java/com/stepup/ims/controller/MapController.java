package com.stepup.ims.controller;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.stepup.ims.service.GoogleMapsService;
import com.stepup.ims.service.UserService;
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
    @Autowired
    private UserService userService;

    @GetMapping("/inspectors")
    public List<GoogleMapsService.InspectorDistance> getInspectorsByAddress(Model model, @RequestParam String address) throws InterruptedException, ApiException, IOException {
        // 1. Geocode the user's input address
        LatLng userLocation = googleMapsService.geocodeAddress(address);

        // 2. Calculate distances from user location to all inspectors
        return googleMapsService.getInspectorDistances(userLocation, userService.getAllUsersLatLng());
    }
}

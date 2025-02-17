package com.stepup.ims.service;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleMapsService {

    private final GeoApiContext geoApiContext;

    public GoogleMapsService(@Value("${google.maps.api.key}") String apiKey) {
        this.geoApiContext = new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    // Geocode address to latitude and longitude
    public LatLng geocodeAddress(String address) throws InterruptedException, ApiException, IOException {
        GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, address).await();
        if (results != null && results.length > 0) {
            return results[0].geometry.location;
        }
        throw new IllegalArgumentException("Address not found: " + address);
    }

    // Calculate distances between a user location and a list of inspector locations
    public List<InspectorDistance> getInspectorDistances(LatLng userLocation, List<LatLng> inspectorLocations) throws InterruptedException, ApiException, IOException {
        List<InspectorDistance> distances = new ArrayList<>();

        LatLng[] destinations = inspectorLocations.toArray(new LatLng[0]);

        DistanceMatrix result = DistanceMatrixApi.newRequest(geoApiContext).origins(userLocation).destinations(destinations).mode(TravelMode.DRIVING).await();

        for (int i = 0; i < destinations.length; i++) {
            distances.add(new InspectorDistance("inspector" + (i + 1), destinations[i], result.rows[0].elements[i].distance.humanReadable, result.rows[0].elements[i].duration.humanReadable));
        }

        return distances;
    }

    public static class InspectorDistance {
        private String name;
        private LatLng location;
        private String distance;
        private String duration;

        public InspectorDistance(String name, LatLng location, String distance, String duration) {
            this.name = name;
            this.location = location;
            this.distance = distance;
            this.duration = duration;
        }

        public String getName() {
            return name;
        }

        public LatLng getLocation() {
            return location;
        }

        public String getDistance() {
            return distance;
        }

        public String getDuration() {
            return duration;
        }
    }
}

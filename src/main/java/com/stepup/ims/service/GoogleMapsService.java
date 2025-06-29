package com.stepup.ims.service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GoogleMapsService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsService.class);

    private final GeoApiContext geoApiContext;

    public GoogleMapsService(@Value("${google.maps.api.key}") String apiKey) {
        logger.info("GeoApiContext initialized with provided API key.");
        this.geoApiContext = new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    // Geocode address to latitude and longitude
    public LatLng geocodeAddress(String address) throws InterruptedException, ApiException, IOException {
        logger.debug("Attempting to geocode address: {}", address);
        GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, address).await();
        if (results != null && results.length > 0) {
            logger.info("Successfully geocoded address: {} to lat/lng: {}", address, results[0].geometry.location);
            return results[0].geometry.location;
        }
        logger.error("Geocoding failed. Address not found: {}", address);
        throw new IllegalArgumentException("Address not found: " + address);
    }

    // Calculate distances between a user location and a list of inspector locations
    public Map<String, List<InspectorDistance>> getInspectorDistances(LatLng inspectionLocation, Map<String, List<Pair<String, LatLng>>> inspectorLocations) throws InterruptedException, ApiException, IOException {
        logger.debug("Calculating distances from inspection location: {} to inspector locations.", inspectionLocation);
        Map<String, List<InspectorDistance>> distances = new HashMap<>();

        Map<String, LatLng[]> destinationsMap = inspectorLocations.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .map(Pair::getRight)
                        .toArray(LatLng[]::new)));
        logger.debug("Prepared destination points for distance matrix calculation.");
        DistanceMatrix result = DistanceMatrixApi.newRequest(geoApiContext).origins(inspectionLocation)
                .destinations(destinationsMap.values().stream().flatMap(Stream::of).toArray(LatLng[]::new))
                .mode(TravelMode.DRIVING).await();
        logger.info("Distance matrix retrieved successfully.");
        int index = 0;
        for (Map.Entry<String, List<Pair<String, LatLng>>> entry : inspectorLocations.entrySet()) {
            List<InspectorDistance> inspectorDistances = new ArrayList<>();
            for (Map.Entry<String, LatLng> destination : entry.getValue()) {
                inspectorDistances.add(new InspectorDistance(
                        destination.getKey(),
                        destination.getValue(),
                        result.rows[0].elements[index].distance != null ? result.rows[0].elements[index].distance.humanReadable : "N/A",
                        result.rows[0].elements[index].duration != null ? result.rows[0].elements[index].duration.humanReadable : "N/A"));
                index++;
            }
            distances.put(entry.getKey(), inspectorDistances);
        }
        logger.info("Calculated distances for {} inspector groups.", distances.size());
        return distances;
    }

    @Data
    @AllArgsConstructor
    public static class InspectorDistance {
        private String name;
        private LatLng location;
        private String distance;
        private String duration;

    }
}

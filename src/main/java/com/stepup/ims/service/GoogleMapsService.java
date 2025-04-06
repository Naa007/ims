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
    public Map<String, List<InspectorDistance>> getInspectorDistances(LatLng inspectionLocation, Map<String, List<Pair<String, LatLng>>> inspectorLocations) throws InterruptedException, ApiException, IOException {
        Map<String, List<InspectorDistance>> distances = new HashMap<>();

        Map<String, LatLng[]> destinationsMap = inspectorLocations.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .map(Pair::getRight)
                        .toArray(LatLng[]::new)));

        DistanceMatrix result = DistanceMatrixApi.newRequest(geoApiContext).origins(inspectionLocation)
                .destinations(destinationsMap.values().stream().flatMap(Stream::of).toArray(LatLng[]::new))
                .mode(TravelMode.DRIVING).await();

        int index = 0;
        for (Map.Entry<String, List<Pair<String, LatLng>>> entry : inspectorLocations.entrySet()) {
            List<InspectorDistance> inspectorDistances = new ArrayList<>();
            for (Map.Entry<String, LatLng> destination : entry.getValue()) {
                inspectorDistances.add(new InspectorDistance(
                        destination.getKey(),
                        destination.getValue(),
                        result.rows[0].elements[index].distance.humanReadable,
                        result.rows[0].elements[index].duration.humanReadable));
                index++;
            }
            distances.put(entry.getKey(), inspectorDistances);
        }

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

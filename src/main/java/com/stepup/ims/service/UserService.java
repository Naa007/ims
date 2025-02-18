package com.stepup.ims.service;

import com.google.maps.model.LatLng;
import com.stepup.ims.entity.User;
import com.stepup.ims.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<LatLng> getAllUsersLatLng() {
        return userRepository.findAll().stream()
                // Filter out users with null latitude or longitude
                .filter(user -> user.getLatitude() != null && user.getLongitude() != null)
                // Map each user to a LatLng object
                .map(user -> new LatLng(user.getLatitude(), user.getLongitude()))
                // Collect the LatLng objects into a list
                .toList();

    }

    /**
     * Find a user by their ID.
     */
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Update user location (latitude and longitude).
     */
    public User updateUserLocation(Long userId, Double latitude, Double longitude) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setLatitude(latitude);
        user.setLongitude(longitude);
        return userRepository.save(user);
    }

    /**
     * Find users near a specific location within a given radius (in kilometers).
     */
    public List<User> findUsersNearLocation(Double latitude, Double longitude, Double distance) {
        return userRepository.findUsersNearLocation(latitude, longitude, distance);
    }

    /**
     * Save or update user details.
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Delete a user by ID.
     */
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }
}

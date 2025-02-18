package com.stepup.ims.repository;

import com.stepup.ims.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom query to find users near a specific location with latitude, longitude, and radius
    @Query("SELECT u FROM User u WHERE " + "(6371 * acos(cos(radians(:latitude)) * cos(radians(u.latitude)) * " + "cos(radians(u.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " + "sin(radians(u.latitude)))) <= :radius")
    List<User> findUsersNearLocation(@Param("latitude") Double latitude, @Param("longitude") Double longitude, @Param("radius") Double radius);

}

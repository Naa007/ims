package com.stepup.ims.repository;

import com.stepup.ims.entity.Inspector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectorRepository extends JpaRepository<Inspector, Long> {
    // Additional custom query methods can be declared here if needed.
}
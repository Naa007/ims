package com.stepup.ims.repository;

import com.stepup.ims.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

}

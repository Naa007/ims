package com.stepup.ims.repository;

import com.stepup.ims.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    List<Inspection> findByCreatedBy(String createdBy);

    List<Inspection> findByProposedCVs_CvReviewBytechnicalCoordinator_empId(String cvReviewBytechnicalCoordinatorId);

}

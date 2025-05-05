package com.stepup.ims.repository;

import com.stepup.ims.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    List<Inspection> findByCreatedBy(String createdBy);

    @Query("SELECT e.email FROM Employee e " +
            "WHERE e.empId IN (" +
            "SELECT pc.cvReviewBytechnicalCoordinator.empId FROM Inspection i JOIN i.proposedCVs pc WHERE i.id = :inspectionId " +
            "UNION " +
            "SELECT i.documentsReviewedByTechnicalCoordinator FROM Inspection i WHERE i.id = :inspectionId " +
            "UNION " +
            "SELECT i.inspectionReviewedBy FROM Inspection i WHERE i.id = :inspectionId)")
    List<String> findAllTechnicalCoordinatorsOfInspection(Long inspectionId);

    List<Inspection> findByProposedCVs_CvReviewBytechnicalCoordinator_EmpIdOrDocumentsReviewedByTechnicalCoordinatorOrInspectionReviewedBy(String cvReviewBytechnicalCoordinatorId, String documentsReviewedByTechnicalCoordinator, String inspectionReviewedBy);

    List<Inspection> findByProposedCVs_Inspector_Email(String email);

}


package com.stepup.ims.repository;

import com.stepup.ims.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query("SELECT i FROM Inspection i JOIN i.inspectionDateAsPerNotification dates " +
            "WHERE dates BETWEEN :startDate AND :endDate " +
            "ORDER BY dates ASC")
    List<Inspection> findByInspectionDateAsPerNotificationBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<Inspection> findByCreatedDate(LocalDateTime createdDate);

    List<Inspection> findByCreatedDateBetween(LocalDateTime from, LocalDateTime to);

    List<Inspection> findByClient_ClientIdAndCreatedDateBetween(Long clientId, LocalDateTime startDate, LocalDateTime endDate);

    List<Inspection> findByCreatedByAndCreatedDateBetween(String createdBy, LocalDateTime from, LocalDateTime to);

    List<Inspection> findByProposedCVs_Inspector_EmailAndCreatedDateBetween(String email, LocalDateTime from, LocalDateTime to);

    @Query("SELECT i FROM Inspection i JOIN i.proposedCVs pc " +
            "WHERE (pc.cvReviewBytechnicalCoordinator.empId = :cvReviewBytechnicalCoordinatorId " +
            "OR i.documentsReviewedByTechnicalCoordinator = :documentsReviewedByTechnicalCoordinator " +
            "OR i.inspectionReviewedBy = :inspectionReviewedBy) " +
            "AND i.createdDate BETWEEN :from AND :to")
    List<Inspection> inspectionsReviewedByTechnicalCoordinatorsBetweenDates(
            @Param("cvReviewBytechnicalCoordinatorId") String cvReviewBytechnicalCoordinatorId,
            @Param("documentsReviewedByTechnicalCoordinator") String documentsReviewedByTechnicalCoordinator,
            @Param("inspectionReviewedBy") String inspectionReviewedBy,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

}


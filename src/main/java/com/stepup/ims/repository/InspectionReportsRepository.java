package com.stepup.ims.repository;

import com.stepup.ims.entity.InspectionReports;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InspectionReportsRepository extends JpaRepository<InspectionReports, Long> {

    @Modifying // Required for update/delete operations
    @Transactional // Ensure transactional context (optional if at service layer)
    @Query("UPDATE InspectionReports ir SET ir.technicalCoordinatorRemarks = :remarks WHERE ir.id = :id")
    int updateTechnicalCoordinatorRemarksById(@Param("id") Long id, @Param("remarks") String remarks);
}
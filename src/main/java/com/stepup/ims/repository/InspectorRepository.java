package com.stepup.ims.repository;

import com.stepup.ims.entity.Inspector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectorRepository extends JpaRepository<Inspector, Long> {

    List<Inspector> findByInspectorType(Inspector.InspectorType inspectorType);

    List<Inspector> findAllByCountryAndInspectorStatus(String country, Inspector.InspectorStatusType inspectorStatus);

    List<Inspector> findAllByInspectorStatus(Inspector.InspectorStatusType inspectorStatusType);

    @Query("SELECT i.id FROM Inspector i WHERE i.email = :email")
    String findInspectorIdByEmail(@Param("email") String email);

    @Query("SELECT i.inspectorName FROM Inspector i WHERE i.email = :email")
    String findInspectorNameByEmail(@Param("email") String email);
}
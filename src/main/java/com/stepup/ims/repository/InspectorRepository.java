package com.stepup.ims.repository;

import com.stepup.ims.entity.Inspector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectorRepository extends JpaRepository<Inspector, Long> {

    List<Inspector> findByInspectorType(Inspector.InspectorType inspectorType);

    List<Inspector> findAllByInspectorStatus(Inspector.InspectorStatusType inspectorStatusType);

}
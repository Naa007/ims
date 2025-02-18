package com.stepup.ims.repository;

import com.stepup.ims.entity.InspectionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectionUserRepository extends JpaRepository<InspectionUser, Long> {

}

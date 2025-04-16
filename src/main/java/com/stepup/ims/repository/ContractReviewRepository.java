package com.stepup.ims.repository;

import com.stepup.ims.entity.ContractReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractReviewRepository extends JpaRepository<ContractReview, Long> {

    Optional<ContractReview> findContractReviewById(Long id);

}

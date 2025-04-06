package com.stepup.ims.repository;

import com.stepup.ims.entity.PQR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PQRRepository extends JpaRepository<PQR, Long> {

    Optional<PQR> findPQRById(Long id);

}

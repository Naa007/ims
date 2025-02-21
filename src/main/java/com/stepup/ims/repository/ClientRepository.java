package com.stepup.ims.repository;

import com.stepup.ims.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Client entities.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}

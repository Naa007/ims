package com.stepup.ims.service;

import com.stepup.ims.entity.Status;
import com.stepup.ims.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    /**
     * Get all statuses.
     */
    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }

    /**
     * Get a status by ID.
     */
    public Optional<Status> getStatusById(Long statusId) {
        return statusRepository.findById(statusId);
    }

    /**
     * Save or update a status.
     */
    public Status saveStatus(Status status) {
        return statusRepository.save(status);
    }

    /**
     * Delete a status by ID.
     */
    public void deleteStatusById(Long statusId) {
        statusRepository.deleteById(statusId);
    }
}

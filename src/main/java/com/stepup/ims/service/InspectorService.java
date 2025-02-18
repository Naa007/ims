package com.stepup.ims.service;

import com.stepup.ims.entity.Inspector;
import com.stepup.ims.repository.InspectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InspectorService {

    private final InspectorRepository inspectorRepository;

    @Autowired
    public InspectorService(InspectorRepository inspectorRepository) {
        this.inspectorRepository = inspectorRepository;
    }

    /**
     * Fetch all inspectors from the database.
     */
    public List<Inspector> getAllInspectors() {
        return inspectorRepository.findAll();
    }

    /**
     * Fetch an Inspector by ID.
     */
    public Optional<Inspector> getInspectorById(Long id) {
        return inspectorRepository.findById(id);
    }

    /**
     * Save a new or updated Inspector to the database.
     */
    public Inspector saveInspector(Inspector inspector) {
        return inspectorRepository.save(inspector);
    }

    /**
     * Delete an inspector by ID.
     */
    public void deleteInspector(Long id) {
        inspectorRepository.deleteById(id);
    }
}
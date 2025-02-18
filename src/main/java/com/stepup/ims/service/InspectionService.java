package com.stepup.ims.service;

import com.stepup.ims.entity.Inspection;
import com.stepup.ims.repository.InspectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InspectionService {

    @Autowired
    private InspectionRepository inspectionRepository;

    /**
     * Get all inspections.
     */
    public List<Inspection> getAllInspections() {
        return inspectionRepository.findAll();
    }

    /**
     * Get an inspection by its ID.
     */
    public Optional<Inspection> getInspectionById(Long inspectionId) {
        return inspectionRepository.findById(inspectionId);
    }

    /**
     * Save or update an inspection.
     */
    public Inspection saveInspection(Inspection inspection) {
        return inspectionRepository.save(inspection);
    }

    /**
     * Delete an inspection by ID.
     */
    public void deleteInspectionById(Long inspectionId) {
        inspectionRepository.deleteById(inspectionId);
    }

}

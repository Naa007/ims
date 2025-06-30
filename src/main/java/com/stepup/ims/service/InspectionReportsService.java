package com.stepup.ims.service;

import com.stepup.ims.repository.InspectionReportsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InspectionReportsService {

    private static final Logger logger = LoggerFactory.getLogger(InspectionReportsService.class);
    @Autowired
    InspectionReportsRepository inspectionReportsRepository;

    public String updateTechnicalCoordinatorRemarksById(Long id, String remarks) {
        try {
            int rowsUpdated = inspectionReportsRepository.updateTechnicalCoordinatorRemarksById(id, remarks);
            if (rowsUpdated > 0) {
                return "Remarks updated successfully!";
            } else {
                return "Update failed. No matching record found.";
            }
        } catch (Exception e) {
            logger.error("Error updating remarks for ID {}: {}", id, e.getMessage());
            return "Failed to update remarks. Please try again.";
        }
    }

}

package com.stepup.ims.service;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.modelmapper.InspectionModelMapper;
import com.stepup.ims.repository.InspectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InspectionService {

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private InspectionModelMapper inspectionModelMapper;


    /**
     * Get all inspections.
     */
    public List<Inspection> getAllInspections() {
        return inspectionModelMapper.toModelList(inspectionRepository.findAll());
    }

    /**
     * Get an inspection by its ID.
     */
    public Optional<Inspection> getInspectionById(Long inspectionId) {
        return inspectionModelMapper.getOptionalModel(inspectionRepository.findById(inspectionId));
    }

    /**
     * Save or update an inspection.
     */
    public Inspection saveInspection(Inspection inspection) {
        var inspectionEntity = inspectionModelMapper.toEntity(inspection);

        var proposedCVs = inspectionEntity.getProposedCVs();
        inspectionEntity.setProposedCVs(null);

        var savedInspectionEntity = inspectionRepository.save(inspectionEntity);

        if (proposedCVs != null) {
            proposedCVs.forEach(cv -> cv.setInspection(savedInspectionEntity));
            savedInspectionEntity.setProposedCVs(proposedCVs);
        }

        return inspectionModelMapper.toModel(inspectionRepository.save(savedInspectionEntity));
    }

}

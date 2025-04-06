package com.stepup.ims.service;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.modelmapper.InspectionModelMapper;
import com.stepup.ims.repository.InspectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InspectionService {

    @Autowired
    private EmployeeService employeeService;

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
     * Get all inspections created by the current user.
     */
    public List<Inspection> getAllInspectionsByCreatedBy() {
        return inspectionModelMapper.toModelList(inspectionRepository.findByCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName()));
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

        if (inspectionEntity.getProposedCVs().size() == 1 && inspectionEntity.getProposedCVs().get(0).getId() == null && inspectionEntity.getProposedCVs().get(0).getInspector().getInspectorId() == null) {
            inspectionEntity.setProposedCVs(null);
        }
        var savedInspectionEntity = inspectionRepository.save(inspectionEntity);

        return inspectionModelMapper.toModel(savedInspectionEntity);
    }


    /**
     * Get all inspections reviewed by the current user.
     */
    public List<Inspection> getInspectionsReviewedByLoggedUser() {
        return inspectionModelMapper.toModelList(
                inspectionRepository.findByProposedCVs_CvReviewBytechnicalCoordinator_empId(
                        employeeService.getEmployeeIdByEmail(
                                SecurityContextHolder.getContext().getAuthentication().getName())));
    }
}

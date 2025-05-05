package com.stepup.ims.service;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.modelmapper.InspectionModelMapper;
import com.stepup.ims.repository.InspectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Inspection saveInspection(Inspection inspection) {
        var inspectionEntity = inspectionModelMapper.toEntity(inspection);

        if (inspectionEntity.getProposedCVs().size() == 1 && inspectionEntity.getProposedCVs().get(0).getId() == null && inspectionEntity.getProposedCVs().get(0).getInspector().getInspectorId() == null) {
            inspectionEntity.setProposedCVs(null);
        }
        if (inspectionEntity.getId() == null) {
            inspectionEntity.setCoordinatorName(employeeService.getEmployeeNameByEmail(SecurityContextHolder.getContext().getAuthentication().getName()));
        }
        var savedInspectionEntity = inspectionRepository.save(inspectionEntity);

        return inspectionModelMapper.toModel(savedInspectionEntity);
    }


    /**
     * Get all inspections reviewed by the current user.
     */
    public List<Inspection> getInspectionsReviewedByLoggedUser() {
        String currentUser = employeeService.getEmployeeIdByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName());
        return inspectionModelMapper.toModelList(
                inspectionRepository.findByProposedCVs_CvReviewBytechnicalCoordinator_EmpIdOrDocumentsReviewedByTechnicalCoordinatorOrInspectionReviewedBy(
                       currentUser, currentUser, currentUser ));
    }

    /**
     * Get all inspections assigned to the current user.
     */
    public List<Inspection> getInspectionsOfInspectorByLoggedUser() {
        return inspectionModelMapper.toModelList(
                inspectionRepository.findByProposedCVs_Inspector_Email(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    public String[] getTechnicalCoordinatorsByInspectionId(Long inspectionId) {
        List<String> techCoordinators = inspectionRepository.findAllTechnicalCoordinatorsOfInspection(inspectionId);
        return techCoordinators == null || techCoordinators.isEmpty()
                ? new String[0]
                : techCoordinators.stream().map(String::trim).distinct().toArray(String[]::new);
    }
}

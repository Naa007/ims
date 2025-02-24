package com.stepup.ims.service;

import com.stepup.ims.model.Inspector;
import com.stepup.ims.modelmapper.InspectorModelMapper;
import com.stepup.ims.repository.InspectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InspectorService {

    @Autowired
    private final InspectorRepository inspectorRepository;
    @Autowired
    private InspectorModelMapper inspectorModelMapper;

    @Autowired
    public InspectorService(InspectorRepository inspectorRepository) {
        this.inspectorRepository = inspectorRepository;
    }

    /**
     * Fetch all inspectors from the database.
     */
    public List<Inspector> getAllInspectors() {
        return inspectorModelMapper.toModelList(inspectorRepository.findAll());
    }

    /**
     * Fetch an Inspector by ID.
     */
    public Optional<Inspector> getInspectorById(Long id) {
        return inspectorRepository.findById(id).map(inspectorModelMapper::toModel);
    }

    /**
     * Save a new or updated Inspector to the database.
     */
    public Inspector saveInspector(Inspector inspector) {
        var inspectorEntity = inspectorModelMapper.toEntity(inspector);

        // Nullify references before saving inspectorEntity
        var certificates = inspectorEntity.getCertificates();
        var mainQualification = inspectorEntity.getMainQualificationCategory();
        var specialQualification = inspectorEntity.getSpecialQualification();

        inspectorEntity.setCertificates(null);
        inspectorEntity.setMainQualificationCategory(null);
        inspectorEntity.setSpecialQualification(null);

        // Save inspectorEntity without references
        var savedInspectorEntity = inspectorRepository.save(inspectorEntity);

        // Restore references and set back associations
        if (certificates != null) {
            certificates.forEach(c -> c.setInspector(savedInspectorEntity));
            savedInspectorEntity.setCertificates(certificates);
        }
        if (mainQualification != null) {
            mainQualification.setInspector(savedInspectorEntity);
            savedInspectorEntity.setMainQualificationCategory(mainQualification);
        }
        if (specialQualification != null) {
            specialQualification.setInspector(savedInspectorEntity);
            savedInspectorEntity.setSpecialQualification(specialQualification);
        }

        // Save the entity again with all references properly associated
        var savedEntity = inspectorRepository.save(savedInspectorEntity);
        return inspectorModelMapper.toModel(savedEntity);
    }

    /**
     * Delete an inspector by ID.
     */
    public void deleteInspector(Long id) {
        inspectorRepository.deleteById(id);
    }
}
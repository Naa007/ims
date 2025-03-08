package com.stepup.ims.service;

import com.google.maps.model.LatLng;
import com.stepup.ims.model.Inspector;
import com.stepup.ims.modelmapper.InspectorModelMapper;
import com.stepup.ims.repository.InspectorRepository;
import org.apache.commons.lang3.tuple.Pair;
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
    public List<Inspector> getAllActiveInspectors() {
        return inspectorModelMapper.toModelList(inspectorRepository.findAllByInspectorStatus(com.stepup.ims.entity.Inspector.InspectorStatusType.ACTIVE));
    }

    /**
     * Fetch all inspectors from the database.
     */
    public List<Inspector> getAllInspectors() {
        return inspectorModelMapper.toModelList(inspectorRepository.findAll());
    }

    /**
     * Fetch all inspectors of type TECHNICAL_COORDINATOR from the database.
     */
    public List<Inspector> getAllTechnicalCoordinators() {
        return inspectorModelMapper.toModelList(inspectorRepository.findByInspectorType(com.stepup.ims.entity.Inspector.InspectorType.TECHNICAL_COORDINATOR));
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

        Optional<com.stepup.ims.entity.Inspector> existingInspectorOpt = inspectorRepository.findById(inspectorEntity.getInspectorId());

        if (existingInspectorOpt.isPresent()) {
            // ✅ Updating existing record
            com.stepup.ims.entity.Inspector existingEntity = existingInspectorOpt.get();

            // ✅ Update inspector fields
            existingEntity.setInspectorName(inspectorEntity.getInspectorName());
            existingEntity.setPhone(inspectorEntity.getPhone());
            existingEntity.setCountry(inspectorEntity.getCountry());
            existingEntity.setInspectorType(inspectorEntity.getInspectorType());
            existingEntity.setInspectorStatus(inspectorEntity.getInspectorStatus());
            existingEntity.setDob(inspectorEntity.getDob());
            existingEntity.setEmail(inspectorEntity.getEmail());
            existingEntity.setEducationDetails(inspectorEntity.getEducationDetails());
            existingEntity.setDisciplines(inspectorEntity.getDisciplines());
            existingEntity.setAddress(inspectorEntity.getAddress());
            existingEntity.setAddressCoordinates(inspectorEntity.getAddressCoordinates());
            existingEntity.setRemarks(inspectorEntity.getRemarks());

            // ✅ Update Main Qualifications
            if (inspectorEntity.getMainQualificationCategory() != null) {
                MainQualifications newMainQualification = inspectorEntity.getMainQualificationCategory();
                if (existingEntity.getMainQualificationCategory() != null) {
                    MainQualifications existingMainQualification = existingEntity.getMainQualificationCategory();
                    existingMainQualification.setElectrical(newMainQualification.getElectrical());
                    existingMainQualification.setMechRotating(newMainQualification.getMechRotating());
                    existingMainQualification.setMechStatic(newMainQualification.getMechStatic());
                    existingMainQualification.setPiping(newMainQualification.getPiping());
                    existingMainQualification.setSteelStructure(newMainQualification.getSteelStructure());
                } else {
                    newMainQualification.setInspector(existingEntity);
                    existingEntity.setMainQualificationCategory(newMainQualification);
                }
            }

            // ✅ Update Special Qualifications
            if (inspectorEntity.getSpecialQualification() != null) {
                SpecialQualification newSpecialQualification = inspectorEntity.getSpecialQualification();

                if (existingEntity.getSpecialQualification() != null) {
                    SpecialQualification existingSpecialQualification = existingEntity.getSpecialQualification();
                    existingSpecialQualification.setAramco(newSpecialQualification.getAramco() != null
                            ? newSpecialQualification.getAramco()
                            : existingSpecialQualification.getAramco());
                    existingSpecialQualification.setSec(newSpecialQualification.getSec() != null
                            ? newSpecialQualification.getSec()
                            : existingSpecialQualification.getSec());
                    existingSpecialQualification.setSpecialQualificationDetails(newSpecialQualification.getSpecialQualificationDetails() != null
                            ? newSpecialQualification.getSpecialQualificationDetails()
                            : existingSpecialQualification.getSpecialQualificationDetails());
                } else {
                    newSpecialQualification.setInspector(existingEntity);
                    existingEntity.setSpecialQualification(newSpecialQualification);
                }
            }

            // ✅ Update Certificates
            if (inspectorEntity.getCertificates() != null) {
                existingEntity.getCertificates().clear();
                existingEntity.getCertificates().addAll(inspectorEntity.getCertificates());
                existingEntity.getCertificates().forEach(c -> c.setInspector(existingEntity));
            }

            // ✅ Save updated entity
            var savedEntity = inspectorRepository.save(existingEntity);
            return inspectorModelMapper.toModel(savedEntity);
        } else {

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
    }


    public List<Pair<String, LatLng>> getActiveInspectorsLatLang() {
        return inspectorRepository.findAllByInspectorStatus(com.stepup.ims.entity.Inspector.InspectorStatusType.ACTIVE).stream()
                .map(inspectorModelMapper::toModel)
                .filter(inspector -> inspector.getAddressCoordinates() != null)
                .map(inspector -> Pair.of(inspector.getInspectorName(), inspector.getAddressCoordinates()))
                .toList();
    }
}
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

        var savedInspectorEntity = inspectorRepository.save(inspectorEntity);

        return inspectorModelMapper.toModel(savedInspectorEntity);
    }


    public List<Pair<String, LatLng>> getActiveInspectorsLatLang() {
        return inspectorRepository.findAllByInspectorStatus(com.stepup.ims.entity.Inspector.InspectorStatusType.ACTIVE).stream()
                .map(inspectorModelMapper::toModel)
                .filter(inspector -> inspector.getAddressCoordinates() != null)
                .map(inspector -> Pair.of(inspector.getInspectorName(), inspector.getAddressCoordinates()))
                .toList();
    }
}
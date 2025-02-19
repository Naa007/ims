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
        var savedEntity = inspectorRepository.save(inspectorEntity);
        return inspectorModelMapper.toModel(savedEntity);
    }

    /**
     * Delete an inspector by ID.
     */
    public void deleteInspector(Long id) {
        inspectorRepository.deleteById(id);
    }
}
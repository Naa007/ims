package com.stepup.ims.service;

import com.google.maps.model.LatLng;
import com.stepup.ims.model.Inspector;
import com.stepup.ims.modelmapper.InspectorModelMapper;
import com.stepup.ims.repository.InspectorRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InspectorService {

    private static final Logger logger = LoggerFactory.getLogger(InspectorService.class);

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
        logger.info("Fetching all active inspectors");
        return inspectorModelMapper.toModelList(inspectorRepository.findAllByInspectorStatus(com.stepup.ims.entity.Inspector.InspectorStatusType.ACTIVE));
    }

    /**
     * Fetch all inspectors from the database.
     */
    public List<Inspector> getAllInspectors() {
        logger.info("Fetching all inspectors");
        return inspectorModelMapper.toModelList(inspectorRepository.findAll());
    }

    /**
     * Fetch all inspectors of type TECHNICAL_COORDINATOR from the database.
     */
    public List<Inspector> getAllTechnicalCoordinators() {
        logger.info("Fetching all technical coordinators");
        return inspectorModelMapper.toModelList(inspectorRepository.findByInspectorType(com.stepup.ims.entity.Inspector.InspectorType.TECHNICAL_COORDINATOR));
    }

    /**
     * Fetch an Inspector by ID.
     */
    public Optional<Inspector> getInspectorById(Long id) {
        logger.info("Fetching inspector by ID: {}", id);
        return inspectorRepository.findById(id).map(inspectorModelMapper::toModel);
    }

    /**
     * Save a new or updated Inspector to the database.
     */
    @Transactional
    public Inspector saveInspector(Inspector inspector) {
        logger.info("Saving inspector");
        var inspectorEntity = inspectorModelMapper.toEntity(inspector);

        var savedInspectorEntity = inspectorRepository.save(inspectorEntity);

        return inspectorModelMapper.toModel(savedInspectorEntity);
    }


    public Map<String, List<Pair<String, LatLng>>> getActiveInspectorsLatLang() {
        logger.info("Fetching coordinates of all active inspectors");
        return  inspectorRepository.findAllByInspectorStatus(com.stepup.ims.entity.Inspector.InspectorStatusType.ACTIVE).stream()
                .map(inspectorModelMapper::toModel)
                .filter(inspector -> inspector.getAddressCoordinates() != null)
                .collect(Collectors.groupingBy(
                        inspector -> inspector.getInspectorType().toString(),
                        Collectors.mapping(
                                inspector -> Pair.of(inspector.getInspectorName(), inspector.getAddressCoordinates()),
                                Collectors.toList()
                        )
                ));
    }

    public List<Inspector> getInspectorsListByCountry(String country) {
        logger.info("Fetching inspectors for country: {}", country);
        return inspectorModelMapper.toModelList(
                inspectorRepository.findAllByCountryAndInspectorStatus(country, com.stepup.ims.entity.Inspector.InspectorStatusType.ACTIVE));
    }

    public String getInspectorIdByEmail(String email) {
        logger.debug("Fetching inspector ID by email: {}", email);
        return inspectorRepository.findInspectorIdByEmail(email);
    }

    public String getInspectorNameByEmail(String email) {
        logger.debug("Fetching inspector name by email: {}", email);
        return inspectorRepository.findInspectorNameByEmail(email);
    }
}
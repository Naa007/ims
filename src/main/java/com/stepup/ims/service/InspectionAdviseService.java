package com.stepup.ims.service;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.model.InspectionAdvise;
import com.stepup.ims.modelmapper.InspectionAdviseModelMapper;
import com.stepup.ims.modelmapper.InspectionModelMapper;
import com.stepup.ims.repository.InspectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class InspectionAdviseService {

    private static final Logger logger = LoggerFactory.getLogger(InspectionAdviseService.class);

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private InspectionAdviseModelMapper inspectionAdviseModelMapper;

    @Autowired
    private InspectionModelMapper inspectionModelMapper;

    @Autowired
    private EmailService emailService;

    public Inspection getInspectionAdviseByInspectionId(Long inspectionId) {
        logger.info("Fetching inspection advise for inspection ID: {}", inspectionId);
        Optional<com.stepup.ims.entity.Inspection> inspection = inspectionRepository.findById(inspectionId);
        if (inspection.isPresent()) {
            logger.debug("Inspection found for ID: {}", inspectionId);
            com.stepup.ims.entity.Inspection inspectionEntity = inspection.get();
            if (inspectionEntity.getInspectionAdvise() == null) {
                logger.info("No existing inspection advise found. Creating new advise for ID: {}", inspectionId);
                com.stepup.ims.entity.InspectionAdvise newInspectionAdvise = new com.stepup.ims.entity.InspectionAdvise();
                newInspectionAdvise.setInspectionId(inspectionId);

                newInspectionAdvise.setScopeOfInspectionList(
                        Arrays.stream(com.stepup.ims.entity.ScopeOfInspection.ScopeOfInspectionTypes.values())
                                .map(descriptionType -> {
                                    com.stepup.ims.entity.ScopeOfInspection document = new com.stepup.ims.entity.ScopeOfInspection();
                                    document.setScopeOfInspection(descriptionType);
                                    document.setStatus(com.stepup.ims.entity.ScopeOfInspection.DocumentStatusTypes.NA);
                                    return document;
                                })
                                .toList()
                );


                newInspectionAdvise.setClientInstructionToInspectorList(
                        Arrays.stream(com.stepup.ims.entity.ClientInstructionToInspector.ClientInstructionTypes.values())
                                .map(descriptionType -> {
                                    com.stepup.ims.entity.ClientInstructionToInspector document = new com.stepup.ims.entity.ClientInstructionToInspector();
                                    document.setClientInstruction(descriptionType);
                                    document.setStatus(com.stepup.ims.entity.ClientInstructionToInspector.DocumentStatusTypes.NA);
                                    return document;
                                })
                                .toList()
                );

                newInspectionAdvise.setAcknowledgeFromInspectorList(
                        Arrays.stream(com.stepup.ims.entity.AcknowledgeFromInspector.AcknowledgementTypes.values())
                                .map(descriptionType -> {
                                    com.stepup.ims.entity.AcknowledgeFromInspector document = new com.stepup.ims.entity.AcknowledgeFromInspector();
                                    document.setAcknowledgement(descriptionType);
                                    document.setStatus(com.stepup.ims.entity.AcknowledgeFromInspector.DocumentStatusTypes.NA);
                                    return document;
                                })
                                .toList()
                );

                inspectionEntity.setInspectionAdvise(newInspectionAdvise);
                inspectionRepository.save(inspectionEntity);
                logger.info("New inspection advise saved for inspection ID: {}", inspectionId);
            }
        }

        return inspectionModelMapper.getOptionalModel(inspection).orElseThrow(() -> new IllegalArgumentException("Inspection not found with id: " + inspectionId));
    }

    public Inspection updateInspectionAdvise(Long inspectionId, InspectionAdvise updatedInspectionAdvise) {
        logger.info("Updating inspection advise for inspection ID: {}", inspectionId);
        Optional<com.stepup.ims.entity.Inspection> inspectionOptional = inspectionRepository.findById(inspectionId);
        com.stepup.ims.entity.Inspection inspectionEntity = inspectionOptional.orElseThrow(() -> new IllegalArgumentException("Inspection not found with id: " + inspectionId));
        inspectionEntity.setInspectionAdvise(inspectionAdviseModelMapper.toEntity(updatedInspectionAdvise));
        com.stepup.ims.entity.Inspection updatedInspectionEntity = inspectionRepository.save(inspectionEntity);
        logger.debug("Inspection advise updated successfully for ID: {}", inspectionId);
        emailService.sendInspectionAdviseNotification(updatedInspectionEntity);
        logger.info("Notification email sent after inspection advise update for ID: {}", inspectionId);
        return inspectionModelMapper.toModel(updatedInspectionEntity);
    }

}

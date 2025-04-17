package com.stepup.ims.service;

import com.stepup.ims.model.Inspection;
import com.stepup.ims.model.InspectionAdvise;
import com.stepup.ims.modelmapper.InspectionAdviseModelMapper;
import com.stepup.ims.modelmapper.InspectionModelMapper;
import com.stepup.ims.repository.InspectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class InspectionAdviseService {

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private InspectionAdviseModelMapper inspectionAdviseModelMapper;

    @Autowired
    private InspectionModelMapper inspectionModelMapper;

    public Inspection getInspectionAdviseByInspectionId(Long inspectionId) {
        Optional<com.stepup.ims.entity.Inspection> inspection = inspectionRepository.findById(inspectionId);
        if (inspection.isPresent()) {
            com.stepup.ims.entity.Inspection inspectionEntity = inspection.get();
            if (inspectionEntity.getInspectionAdvise() == null) {
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
            }
        }

        return inspectionModelMapper.getOptionalModel(inspection).orElseThrow(() -> new IllegalArgumentException("Inspection not found with id: " + inspectionId));
    }

    public Inspection updateInspectionContactReview(Long inspectionId, InspectionAdvise updatedInspectionAdvise) {
        Optional<com.stepup.ims.entity.Inspection> inspectionOptional = inspectionRepository.findById(inspectionId);
        com.stepup.ims.entity.Inspection inspectionEntity = inspectionOptional.orElseThrow(() -> new IllegalArgumentException("Inspection not found with id: " + inspectionId));
        inspectionEntity.setInspectionAdvise(inspectionAdviseModelMapper.toEntity(updatedInspectionAdvise));
        return inspectionModelMapper.toModel(inspectionRepository.save(inspectionEntity));
    }

}

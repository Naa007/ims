package com.stepup.ims.service;

import com.stepup.ims.model.ContractReview;
import com.stepup.ims.model.Inspection;
import com.stepup.ims.modelmapper.ContractReviewModelMapper;
import com.stepup.ims.modelmapper.InspectionModelMapper;
import com.stepup.ims.repository.InspectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class ContractReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ContractReviewService.class);

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private ContractReviewModelMapper contractReviewModelMapper;
    
    @Autowired
    private InspectionModelMapper inspectionModelMapper;

    @Autowired
    private EmailService emailService;

    public Inspection getContractReviewByInspectionId(Long inspectionId) {
        logger.debug("Fetching inspection with ID: {}", inspectionId);
        Optional<com.stepup.ims.entity.Inspection> inspection = inspectionRepository.findById(inspectionId);
        if (inspection.isPresent()) {
            logger.info("Inspection found with ID: {}", inspectionId);
            com.stepup.ims.entity.Inspection inspectionEntity = inspection.get();
            if (inspectionEntity.getContractReview() == null) {
                logger.info("No ContractReview found for inspection ID: {}, initializing new ContractReview", inspectionId);

                com.stepup.ims.entity.ContractReview newContractReview = new com.stepup.ims.entity.ContractReview();

                newContractReview.setInspectionId(inspectionId);

                newContractReview.setContractDocumentList(
                        Arrays.stream(com.stepup.ims.entity.ContractDocument.DocumentDescriptionTypes.values())
                                .map(descriptionType -> {
                                    com.stepup.ims.entity.ContractDocument document = new com.stepup.ims.entity.ContractDocument();
                                    document.setDocumentDescription(descriptionType);
                                    document.setStatus(com.stepup.ims.entity.ContractDocument.DocumentStatusTypes.NA);
                                    document.setSpecialRemarks("");
                                    return document;
                                })
                                .toList()
                );
                inspectionEntity.setContractReview(newContractReview);
                inspectionRepository.save(inspectionEntity);
                logger.info("New ContractReview created and saved for inspection ID: {}", inspectionId);
            }
        }

        return inspectionModelMapper.getOptionalModel(inspection)
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found with id: " + inspectionId));
    }

    public Inspection updateInspectionContactReview(Long inspectionId, ContractReview updatedContractReview) {
        logger.debug("Updating ContractReview for inspection ID: {}", inspectionId);
        Optional<com.stepup.ims.entity.Inspection> inspectionOptional = inspectionRepository.findById(inspectionId);
        com.stepup.ims.entity.Inspection inspectionEntity = inspectionOptional.orElseThrow(() ->
                new IllegalArgumentException("Inspection not found with id: " + inspectionId));
        inspectionEntity.setContractReview(contractReviewModelMapper.toEntity(updatedContractReview));
        com.stepup.ims.entity.Inspection updatedInspectionEntity = inspectionRepository.save(inspectionEntity);
        logger.info("ContractReview updated for inspection ID: {}", inspectionId);
        emailService.sendContractReviewNotification(updatedInspectionEntity);
        return inspectionModelMapper.toModel(updatedInspectionEntity);
    }

}

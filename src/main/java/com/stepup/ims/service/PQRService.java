package com.stepup.ims.service;

import com.stepup.ims.model.Inspector;
import com.stepup.ims.model.PQR;
import com.stepup.ims.modelmapper.InspectorModelMapper;
import com.stepup.ims.modelmapper.PQRModelMapper;
import com.stepup.ims.repository.InspectorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PQRService {

    private static final Logger logger = LoggerFactory.getLogger(PQRService.class);

    @Autowired
    private InspectorRepository inspectorRepository;

    @Autowired
    private PQRModelMapper pqrModelMapper;
    
    @Autowired
    private InspectorModelMapper inspectorModelMapper;

    public Inspector getPQRByInspectorId(Long inspectorId) {
        logger.info("Fetching PQR for inspector");
        Optional<com.stepup.ims.entity.Inspector> inspector = inspectorRepository.findById(inspectorId);
        if (inspector.isPresent()) {
            com.stepup.ims.entity.Inspector inspectorEntity = inspector.get();
            if (inspectorEntity.getPqr() == null) {
                logger.info("No existing PQR found. Initializing new PQR.");
                com.stepup.ims.entity.PQR newPqr = new com.stepup.ims.entity.PQR();
                newPqr.setInspectorId(inspectorId);
                inspectorEntity.setPqr(newPqr);
                inspectorRepository.save(inspectorEntity);
                logger.debug("New PQR initialized and saved.");
            }
        }

        return inspectorModelMapper.toOptionalModel(inspector)
                .orElseThrow(() -> new IllegalArgumentException("Inspector not found with id: " + inspectorId));
    }

    public Inspector updateInspectorPQR(Long inspectorId, PQR updatedPQR) {
        logger.info("Updating PQR for inspector");
        Optional<com.stepup.ims.entity.Inspector> inspectorOptional = inspectorRepository.findById(inspectorId);
        com.stepup.ims.entity.Inspector inspectorEntity = inspectorOptional.orElseThrow(() ->
                new IllegalArgumentException("Inspector not found with id: " + inspectorId));
        inspectorEntity.setPqr(pqrModelMapper.toEntity(updatedPQR));
        logger.debug("PQR entity set on inspector and saving.");
        return inspectorModelMapper.toModel(inspectorRepository.save(inspectorEntity));
    }

}

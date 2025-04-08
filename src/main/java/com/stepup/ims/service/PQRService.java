package com.stepup.ims.service;

import com.stepup.ims.model.Inspector;
import com.stepup.ims.model.PQR;
import com.stepup.ims.modelmapper.InspectorModelMapper;
import com.stepup.ims.modelmapper.PQRModelMapper;
import com.stepup.ims.repository.InspectorRepository;
import com.stepup.ims.repository.PQRRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PQRService {

    @Autowired
    private PQRRepository pqrRepository;
    
    @Autowired
    private InspectorRepository inspectorRepository;

    @Autowired
    private PQRModelMapper pqrModelMapper;
    
    @Autowired
    private InspectorModelMapper inspectorModelMapper;

    public Inspector getPQRByInspectorId(Long inspectorId) {
        Optional<com.stepup.ims.entity.Inspector> inspector = inspectorRepository.findById(inspectorId);
        if (inspector.isPresent()) {
            com.stepup.ims.entity.Inspector inspectorEntity = inspector.get();
            if (inspectorEntity.getPqr() == null) {
                com.stepup.ims.entity.PQR newPqr = new com.stepup.ims.entity.PQR();
                newPqr.setInspectorId(inspectorId);
                inspectorEntity.setPqr(newPqr);
                inspectorRepository.save(inspectorEntity);
            }
        }

        return inspectorModelMapper.toOptionalModel(inspector)
                .orElseThrow(() -> new IllegalArgumentException("Inspector not found with id: " + inspectorId));
    }

    public Inspector updateInspectorPQR(Long inspectorId, PQR updatedPQR) {
        Optional<com.stepup.ims.entity.Inspector> inspectorOptional = inspectorRepository.findById(inspectorId);
        com.stepup.ims.entity.Inspector inspectorEntity = inspectorOptional.orElseThrow(() ->
                new IllegalArgumentException("Inspector not found with id: " + inspectorId));
        inspectorEntity.setPqr(pqrModelMapper.toEntity(updatedPQR));
        return inspectorModelMapper.toModel(inspectorRepository.save(inspectorEntity));
    }

}

package com.stepup.ims.modelmapper;

import com.stepup.ims.model.InspectionAdvise;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InspectionAdviseModelMapper {

    @Autowired
    private ModelMapper modelMapper;

    public InspectionAdvise toModel(com.stepup.ims.entity.InspectionAdvise inspectionAdviseEntity) {
        return modelMapper.map(inspectionAdviseEntity, InspectionAdvise.class);
    }

    public com.stepup.ims.entity.InspectionAdvise toEntity(InspectionAdvise inspectionAdvise) {
        return modelMapper.map(inspectionAdvise, com.stepup.ims.entity.InspectionAdvise.class);
    }

    public Optional<InspectionAdvise> optionalToModel(Optional<com.stepup.ims.entity.InspectionAdvise> inspectionAdviseEntity) {
        return inspectionAdviseEntity.map(entity -> modelMapper.map(entity, InspectionAdvise.class));
    }

    public Optional<com.stepup.ims.entity.InspectionAdvise> optionalToEntity(Optional<InspectionAdvise> inspectionAdvise) {
        return inspectionAdvise.map(model -> modelMapper.map(model, com.stepup.ims.entity.InspectionAdvise.class));
    }
}

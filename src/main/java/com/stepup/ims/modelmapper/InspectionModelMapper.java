package com.stepup.ims.modelmapper;

import com.stepup.ims.model.Inspection;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InspectionModelMapper {

    @Autowired
    private ModelMapper modelMapper;

    public com.stepup.ims.entity.Inspection toEntity(Inspection inspection) {
        if (inspection == null) {
            throw new IllegalArgumentException("Input InspectionModel cannot be null");
        }

        return modelMapper.map(inspection, com.stepup.ims.entity.Inspection.class);
    }

    public Inspection toModel(com.stepup.ims.entity.Inspection entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Input InspectionEntity cannot be null");
        }
        return modelMapper.map(entity, Inspection.class);
    }


    public List<com.stepup.ims.entity.Inspection> toEntityList(List<Inspection> inspections) {
        if (inspections == null) {
            throw new IllegalArgumentException("Input InspectionModel List cannot be null");
        }
        return inspections.stream().map(this::toEntity).toList();
    }


    public List<Inspection> toModelList(List<com.stepup.ims.entity.Inspection> entities) {
        if (entities == null) {
            throw new IllegalArgumentException("Input InspectionEntity List cannot be null");
        }
        return entities.stream().map(this::toModel).toList();
    }

    public Optional<Inspection> getOptionalModel(Optional<com.stepup.ims.entity.Inspection> optionalInspectionEntity) {
        return optionalInspectionEntity.map(this::toModel);
    }

    public Optional<com.stepup.ims.entity.Inspection> getOptionalEntity(Optional<Inspection> optionalInspection) {
        return optionalInspection.map(this::toEntity);
    }
}

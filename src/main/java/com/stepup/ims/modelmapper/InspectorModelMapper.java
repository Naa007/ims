package com.stepup.ims.modelmapper;

import com.stepup.ims.model.Inspector;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InspectorModelMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Optional<Inspector> toOptionalModel(Optional<com.stepup.ims.entity.Inspector> optionalInspectorEntity) {
        return optionalInspectorEntity.map(this::toModel);
    }

    public Optional<com.stepup.ims.entity.Inspector> toOptionalEntity(Optional<Inspector> optionalInspectorModel) {
        return optionalInspectorModel.map(this::toEntity);
    }

    public Inspector toModel(com.stepup.ims.entity.Inspector inspectorEntity) {
        if (inspectorEntity == null) {
            throw new IllegalArgumentException("Input inspectorEntity cannot be null");
        }
        return modelMapper.map(inspectorEntity, Inspector.class);
    }

    public com.stepup.ims.entity.Inspector toEntity(Inspector inspectorModel) {
        if (inspectorModel == null) {
            throw new IllegalArgumentException("Input inspectorModel cannot be null");
        }
        return modelMapper.map(inspectorModel, com.stepup.ims.entity.Inspector.class);
    }

    public List<Inspector> toModelList(List<com.stepup.ims.entity.Inspector> entityInspectors) {
        if (entityInspectors == null) {
            throw new IllegalArgumentException("Input entityInspectors list cannot be null");
        }
        return entityInspectors.stream()
                .map(this::toModel)
                .toList();
    }

    public List<com.stepup.ims.entity.Inspector> toEntityList(List<Inspector> modelInspectors) {
        if (modelInspectors == null) {
            throw new IllegalArgumentException("Input modelInspectors list cannot be null");
        }
        return modelInspectors.stream()
                .map(this::toEntity)
                .toList();
    }
}

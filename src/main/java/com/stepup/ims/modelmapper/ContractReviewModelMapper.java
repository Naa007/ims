package com.stepup.ims.modelmapper;

import com.stepup.ims.model.ContractReview;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ContractReviewModelMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ContractReview toModel(com.stepup.ims.entity.ContractReview contractReviewEntity) {
        return modelMapper.map(contractReviewEntity, ContractReview.class);
    }

    public com.stepup.ims.entity.ContractReview toEntity(ContractReview contractReview) {
        return modelMapper.map(contractReview, com.stepup.ims.entity.ContractReview.class);
    }

    public Optional<ContractReview> optionalToModel(Optional<com.stepup.ims.entity.ContractReview> contractReviewEntity) {
        return contractReviewEntity.map(entity -> modelMapper.map(entity, ContractReview.class));
    }

    public Optional<com.stepup.ims.entity.ContractReview> optionalToEntity(Optional<ContractReview> contractReview) {
        return contractReview.map(model -> modelMapper.map(model, com.stepup.ims.entity.ContractReview.class));
    }
}

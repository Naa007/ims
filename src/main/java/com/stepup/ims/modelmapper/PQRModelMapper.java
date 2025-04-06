package com.stepup.ims.modelmapper;

import com.stepup.ims.model.PQR;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PQRModelMapper {

    @Autowired
    private ModelMapper modelMapper;

    public PQR toModel(com.stepup.ims.entity.PQR pqrEntity) {
        return modelMapper.map(pqrEntity, PQR.class);
    }

    public com.stepup.ims.entity.PQR toEntity(PQR pqr) {
        return modelMapper.map(pqr, com.stepup.ims.entity.PQR.class);
    }

    public Optional<PQR> optionalToModel(Optional<com.stepup.ims.entity.PQR> pqrEntity) {
        return pqrEntity.map(entity -> modelMapper.map(entity, PQR.class));
    }

    public Optional<com.stepup.ims.entity.PQR> optionalToEntity(Optional<PQR> pqr) {
        return pqr.map(model -> modelMapper.map(model, com.stepup.ims.entity.PQR.class));
    }

}

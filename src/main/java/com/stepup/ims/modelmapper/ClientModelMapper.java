package com.stepup.ims.modelmapper;

import com.stepup.ims.model.Client;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientModelMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Client toModel(com.stepup.ims.entity.Client clientEntity) {
        if (clientEntity == null) {
            throw new IllegalArgumentException("Input clientEntity cannot be null");
        }
        return modelMapper.map(clientEntity, Client.class);
    }

    public com.stepup.ims.entity.Client toEntity(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Input clientmodel cannot be null");
        }
        return modelMapper.map(client, com.stepup.ims.entity.Client.class);
    }

    public List<Client> toModelList(List<com.stepup.ims.entity.Client> entityClients) {
        if (entityClients == null) {
            throw new IllegalArgumentException("Input entityClients list cannot be null");
        }
        return entityClients.stream().map(this::toModel).toList();
    }

    public List<com.stepup.ims.entity.Client> toEntityList(List<Client> modelClients) {
        if (modelClients == null) {
            throw new IllegalArgumentException("Input clientModel cannot be null");
        }
        return modelClients.stream().map(this::toEntity).toList();
    }
}

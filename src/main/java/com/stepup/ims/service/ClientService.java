package com.stepup.ims.service;

import com.stepup.ims.controller.AdminRoleController;
import com.stepup.ims.model.Client;
import com.stepup.ims.modelmapper.ClientModelMapper;
import com.stepup.ims.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientModelMapper clientModelMapper;

    /**
     * Fetch all clients from the database.
     */
    public List<Client> getAllClients() {
        logger.info("Fetching all clients");
        return clientModelMapper.toModelList(clientRepository.findAll());
    }

    /**
     * Fetch a Client by ID.
     */
    public Optional<Client> getClientById(Long id) {
        logger.info("Fetching client with ID: {}", id);
        return clientRepository.findById(id).map(clientModelMapper::toModel);
    }

    /**
     * Save a new or updated Client to the database.
     */
    public Client saveClient(Client client) {
        logger.info("Saving client");
        var clientEntity = clientModelMapper.toEntity(client);
        var savedClientEntity = clientRepository.save(clientEntity);
        return clientModelMapper.toModel(savedClientEntity);
    }

    /**
     * Delete a client by ID.
     */
    public void deleteClientById(Long id) {
        clientRepository.deleteById(id);
    }
}

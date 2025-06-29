package com.stepup.ims.service;

import com.stepup.ims.model.AppUser;
import com.stepup.ims.modelmapper.AppUserModelMapper;
import com.stepup.ims.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserService {

    private static final Logger logger = LoggerFactory.getLogger(AppUserService.class);

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppUserModelMapper modelMapper;

    public Optional<AppUser> findByEmail(String email) {
        logger.debug("Attempting to find AppUser by email: {}", email);
        return appUserRepository.findById(email).map(modelMapper::toModel);
    }

    public AppUser save(AppUser appUser) {
        logger.debug("Saving AppUser with email");
        return modelMapper.toModel(appUserRepository.save(modelMapper.toDTO(appUser)));
    }
}

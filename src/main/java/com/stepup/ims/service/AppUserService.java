package com.stepup.ims.service;

import com.stepup.ims.model.AppUser;
import com.stepup.ims.modelmapper.AppUserModelMapper;
import com.stepup.ims.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppUserModelMapper modelMapper;

    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findById(email).map(modelMapper::toModel);
    }

    public AppUser save(AppUser appUser) {
        return modelMapper.toModel(appUserRepository.save(modelMapper.toDTO(appUser)));
    }
}

package com.stepup.ims.modelmapper;

import com.stepup.ims.model.AppUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppUserModelMapper {
    @Autowired
    ModelMapper modelMapper;

    public AppUser toModel(com.stepup.ims.entity.AppUser appUserEntity) {
        return modelMapper.map(appUserEntity, AppUser.class);
    }

    public com.stepup.ims.entity.AppUser toDTO(AppUser appUser) {
        return modelMapper.map(appUser, com.stepup.ims.entity.AppUser.class);
    }

}

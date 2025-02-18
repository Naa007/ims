package com.stepup.ims.service;

import com.stepup.ims.entity.InspectionUser;
import com.stepup.ims.repository.InspectionUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InspectionUserService {

    @Autowired
    private InspectionUserRepository inspectionUserRepository;

    /**
     * Assign a user to an inspection.
     */
    public InspectionUser assignUserToInspection(InspectionUser inspectionUser) {
        return inspectionUserRepository.save(inspectionUser);
    }

    /**
     * Remove a user from an inspection.
     */
    public void removeUserFromInspection(Long id) {
        inspectionUserRepository.deleteById(id);
    }

}

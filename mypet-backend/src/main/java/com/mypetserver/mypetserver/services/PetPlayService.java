package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetPlayService {
    private final PetRepository petRepository;
    private final PetManagerService petManagerService;

    public PetPlayService(PetRepository petRepository, PetManagerService petManagerService) {
        this.petRepository = petRepository;
        this.petManagerService = petManagerService;
    }

    @Transactional
    public Boolean petAPet(int petId) {
        petRepository.petAPet(petId);
        this.petManagerService.updatePetFromRepo(petId);
        return true;
    }
}

package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Owner;
import com.mypetserver.mypetserver.repository.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class defines the service which manages interactions between the server and the owners repository (table)
 */
@Service
public class OwnerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OwnerRepository ownerRepository;

    @Autowired
    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    public void saveOwner(Owner owner) {
        try {
            Owner existingOwner = ownerRepository.findOwnerByUsername(owner.getUsername());
            if (existingOwner != null) {
                logger.error("Owner {} already exists!", existingOwner.getUsername());
                throw new IllegalStateException("Owner with username '" + owner.getUsername() + "' already exists");
            }
            ownerRepository.save(owner);
        } catch (Exception e) {
            logger.error("Could not save owner: {}", e.getMessage());
            throw e;
        }
    }
}

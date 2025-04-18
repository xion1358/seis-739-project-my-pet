package com.mypetserver.mypetserver.managers;

import com.mypetserver.mypetserver.dto.Pet;
import com.mypetserver.mypetserver.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class PetManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<Integer, Pet> pets = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final PetRepository petRepository;
    private final Map<Integer, Integer> movementCounters = new HashMap<>();

    public PetManager(SimpMessagingTemplate messagingTemplate,
                      PetRepository petRepository) {
        this.messagingTemplate = messagingTemplate;
        this.petRepository = petRepository;
        startPetLoop();
    }

    private void startPetLoop() {
        logger.info("Starting Pet Loop");
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (Pet pet : pets.values()) {
                updatePetMovement(pet);
                updatePetHunger(pet);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void updatePetMovement(Pet pet) {
        boolean shouldRest = random.nextDouble() < 0.3;

        if (!shouldRest) {
            int locationToMoveTo = 100 + random.nextInt(500);
            String direction = locationToMoveTo > pet.getPetXLocation() ? "right" : "left";
            pet.setPetXLocation(locationToMoveTo);
            pet.setPetYLocation(390);
            pet.setPetDirection(direction);

            int count = movementCounters.getOrDefault(pet.getPetId(), 0) + 1;
            movementCounters.put(pet.getPetId(), count);

            if (count >= 6) {
                petRepository.save(pet);
                movementCounters.put(pet.getPetId(), 0);
            }

            messagingTemplate.convertAndSend("/topic/pet/" + pet.getPetId(), pet);
        }
    }

    private void updatePetHunger(Pet pet) {

    }

    public Pet registerPet(int petId) {
        if (!pets.containsKey(petId)) {
            pets.put(petId, petRepository.getPetByPetId(petId));
        }
        return pets.get(petId);
    }

    public void unregisterPet(int petId) {
        pets.remove(petId);
    }

    public ArrayList<Pet> getPets(String ownerName) {
        return new ArrayList<>(petRepository.getPetsByPetOwner(ownerName));
    }
}

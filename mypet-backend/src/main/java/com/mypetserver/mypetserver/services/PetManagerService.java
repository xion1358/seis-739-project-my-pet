package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Pet;
import com.mypetserver.mypetserver.models.PetData;
import com.mypetserver.mypetserver.repository.FoodRepository;
import com.mypetserver.mypetserver.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class PetManagerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<Integer, Pet> pets = new ConcurrentHashMap<>();
    private final PetRepository petRepository;
    private final FoodRepository foodRepository;
    private final PetFoodService petFoodService;
    private final PetBehaviorService petBehaviorService;

    private final Map<Integer, Integer> movementCounters = new HashMap<>();
    private final Map<Integer, Set<String>> petSubscribers = new ConcurrentHashMap<>();

    public PetManagerService(
            SimpMessagingTemplate messagingTemplate,
            PetRepository petRepository,
            FoodRepository foodRepository,
            PetFoodService petFoodService,
            PetBehaviorService petBehaviorService) {
        this.messagingTemplate = messagingTemplate;
        this.petRepository = petRepository;
        this.foodRepository = foodRepository;
        this.petFoodService = petFoodService;
        this.petBehaviorService = petBehaviorService;
        startPetLoop();
    }

    // Main loop
    private void startPetLoop() {
        logger.info("Starting Pet Loop");
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (Pet pet : pets.values()) {
                int updateCycleCount = movementCounters.getOrDefault(pet.getPetId(), 0) + 1;

                this.petFoodService.getAllPetsFoods().computeIfAbsent(pet.getPetId(), id -> foodRepository.findFoodByPetId(id));

                this.updatePet(pet, updateCycleCount);

                movementCounters.put(pet.getPetId(), updateCycleCount);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void updatePet(Pet pet, int updateCycleCount) {
        if (updateCycleCount % 2 == 0) {
            if (pet.getPetHungerLevel() > 0){
                pet.setPetHungerLevel(pet.getPetHungerLevel() - 1);
            }
            if (pet.getPetAffectionLevel() > 0) {
                pet.setPetAffectionLevel(pet.getPetAffectionLevel() - 1);
            }
        }

        PetData updatedPetData = this.petBehaviorService.updatePetBehavior(pet);
        this.sendPetData(updatedPetData);
    }

    // Final action to send pet data
    private void sendPetData(PetData updatedPetData) {
        Map<String, Object> petData = new HashMap<>();
        petData.put("pet", updatedPetData.getPet());
        petData.put("food", updatedPetData.getFood());
        petData.put("action", updatedPetData.getPetBehavior());
        petData.put("actionTime", updatedPetData.getActionTime());
        messagingTemplate.convertAndSend("/topic/pet/" + updatedPetData.getPet().getPetId(), petData);
    }

    // Actions to add/remove pet from update cycle
    public Pet registerPet(int petId) {
        if (!pets.containsKey(petId)) {
            pets.put(petId, petRepository.findByPetId(petId));
        }
        return pets.get(petId);
    }

    public void unregisterPet(int petId) {
        pets.remove(petId);
    }

    public ArrayList<Pet> getPets(String ownerName) {
        return new ArrayList<>(petRepository.getPetsByPetOwner(ownerName));
    }

    public synchronized void addSubscriber(int petId, String sessionId) {
        petSubscribers.computeIfAbsent(petId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public synchronized void updatePetFromRepo(int petId) {
        pets.put(petId, petRepository.findByPetId(petId));
    }

    @Transactional
    public synchronized void removeSubscriberBySessionId(String sessionId) {
        Iterator<Map.Entry<Integer, Set<String>>> iterator = petSubscribers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Set<String>> entry = iterator.next();
            int petId = entry.getKey();
            Set<String> sessions = entry.getValue();

            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    logger.info("Pet {} has been removed from loop because no live sessions", petId);
                    petRepository.save(petRepository.findByPetId(petId));
                    iterator.remove();
                    movementCounters.remove(petId);
                    this.petFoodService.getAllPetsFoods().remove(petId);
                    this.petBehaviorService.getAllPetActions().remove(petId);
                    unregisterPet(petId);
                }
            }
        }
    }

}

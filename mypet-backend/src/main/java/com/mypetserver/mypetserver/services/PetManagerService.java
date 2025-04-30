package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Food;
import com.mypetserver.mypetserver.entities.Pet;
import com.mypetserver.mypetserver.models.PetActions;
import com.mypetserver.mypetserver.models.PetBehavior;
import com.mypetserver.mypetserver.repository.FoodRepository;
import com.mypetserver.mypetserver.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class PetManagerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final int PET_EATING_TIME = 4000;
    public static final int PET_MOVING_TIME = 6000;

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
                PetBehavior petBehavior = this.petBehaviorService.getPetBehavior(pet);

                this.petBehaviorService.updatePetHunger(pet, updateCycleCount);
                this.petFoodService.getAllPetsFoods().computeIfAbsent(pet.getPetId(), id -> foodRepository.getFoodsByPetId(id));
                List<Food> petFood = this.petFoodService.getAllPetsFoods().getOrDefault(pet.getPetId(), new ArrayList<>());

                if (petBehavior != null && petBehavior.getTimeToNextActionInMillis() - System.currentTimeMillis() < 0) {
                    PetBehavior generatedBehavior = this.petBehaviorService.generateBehavior(pet, petFood);

                    petRepository.save(pet);
                    sendPetData(pet, petFood,
                            generatedBehavior.getPetAction().getValue(),
                            generatedBehavior.getTimeToNextActionInMillis()-System.currentTimeMillis());
                } else {
                    PetBehavior noBehavior = new PetBehavior(0, PetActions.IDLE);
                    sendPetData(pet, petFood,
                            noBehavior.getPetAction().getValue(),
                            0);
                }

                movementCounters.put(pet.getPetId(), updateCycleCount);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    // Final action to send pet data
    private void sendPetData(Pet pet, List<Food> petFood, String action, long actionTime) {
        Map<String, Object> petData = new HashMap<>();
        petData.put("pet", pet);
        petData.put("food", petFood);
        petData.put("action", action);
        petData.put("actionTime", actionTime);
        messagingTemplate.convertAndSend("/topic/pet/" + pet.getPetId(), petData);
    }

    // Actions to add/remove pet from update cycle
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

    public synchronized void addSubscriber(int petId, String sessionId) {
        petSubscribers.computeIfAbsent(petId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public synchronized void removeSubscriber(int petId, String sessionId) {
        Set<String> sessions = petSubscribers.get(petId);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                //logger.info("pet {} has no subscribers, removing", petId);
                petRepository.save(petRepository.getPetByPetId(petId));
                petSubscribers.remove(petId);
                movementCounters.remove(petId);
                this.petFoodService.getAllPetsFoods().remove(petId);
                this.petBehaviorService.getAllPetActions().remove(petId);
                unregisterPet(petId);
            }
        }
    }

    public synchronized void removeSubscriberBySessionId(String sessionId) {
        for (Map.Entry<Integer, Set<String>> entry : petSubscribers.entrySet()) {
            removeSubscriber(entry.getKey(), sessionId);
        }
    }
}

package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Pet;
import com.mypetserver.mypetserver.entities.PetTypes;
import com.mypetserver.mypetserver.models.PetActions;
import com.mypetserver.mypetserver.models.PetData;
import com.mypetserver.mypetserver.repository.FoodRepository;
import com.mypetserver.mypetserver.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This service class is used to manage the pet in general. It utilizes other services to manage specific parts.
 * This service manages the high level pet game loop.
 */
@Service
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
                int petIsShared = pet.getShared();

                String topicLocation = (petIsShared > 0) ? "/topic/shared/pet/" : "/topic/pet/";

                int updateCycleCount = movementCounters.getOrDefault(pet.getPetId(), 0) + 1;
                this.petFoodService.getAllPetsFoods().computeIfAbsent(pet.getPetId(), id -> foodRepository.findFoodByPetId(id));


                PetData updatedData = this.updatePet(pet, updateCycleCount);
                this.sendPetData(updatedData, topicLocation);

                movementCounters.put(pet.getPetId(), updateCycleCount);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private PetData updatePet(Pet pet, int updateCycleCount) {
        if (updateCycleCount % 2 == 0) {
            if (pet.getPetHungerLevel() > 0){
                pet.setPetHungerLevel(pet.getPetHungerLevel() - 1);
            }
            if (pet.getPetAffectionLevel() > 0) {
                pet.setPetAffectionLevel(pet.getPetAffectionLevel() - 1);
            }
        }

        return this.petBehaviorService.updatePetBehavior(pet);
    }

    // Final action to send pet data
    private void sendPetData(PetData updatedPetData, String topicLocation) {
        Map<String, Object> petData = new HashMap<>();
        petData.put("pet", updatedPetData.getPet());
        petData.put("food", updatedPetData.getFood());
        petData.put("action", updatedPetData.getPetBehavior());
        petData.put("actionTime", updatedPetData.getActionTime());
        messagingTemplate.convertAndSend(topicLocation + updatedPetData.getPet().getPetId(), petData);
    }

    // Actions to add/remove pet from update cycle
    public Pet registerPet(String ownerName, int petId) {
        Pet pet = petRepository.findByPetId(petId);

        if (ownerName != null && !ownerName.isEmpty() && pet != null) {
            if (!pet.getPetOwner().equals(ownerName) && pet.getShared() < 1) {
                return null;
            } else if (!pets.containsKey(petId)) {
                pets.put(petId, petRepository.findByPetId(petId));
            }
            return pet;
        }
        return null;
    }

    public void unregisterPet(int petId) {
        pets.remove(petId);
    }

    public ArrayList<Pet> getPets(String ownerName) {
        return new ArrayList<>(petRepository.getPetsByPetOwner(ownerName));
    }

    public Map<String, Object> getSharedPets(int cursor, String direction) {
        Map<String, Object> result = new HashMap<>();
        List<Pet> pets = new ArrayList<>();

        if (direction.equals("next")) {
            pets = new ArrayList<>(petRepository.findNextSharedPets(cursor, 5));
        } else {
            pets = new ArrayList<>(petRepository.findPreviousSharedPets(cursor, 5));
        }

        // Special case if user is in an unknown state, return nothing
        if (pets.isEmpty()) {
            result.put("pets", pets);
            result.put("hasNext", false);
            result.put("hasPrevious", false);
            return result;
        }

        int lowestPetId = Integer.MAX_VALUE;
        for (Pet pet : pets) {
            if (pet.getPetId() < lowestPetId) {
                lowestPetId = pet.getPetId();
            }
        }

        int highestPetId = -1;
        for (Pet pet : pets) {
            if (pet.getPetId() > highestPetId) {
                highestPetId = pet.getPetId();
            }
        }

        result.put("pets", pets);
        result.put("hasPrevious", !pets.isEmpty() && !petRepository.findPreviousSharedPets(lowestPetId, 5).isEmpty());
        result.put("hasNext", !petRepository.findNextSharedPets(highestPetId, 5).isEmpty());

        return result;
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
                    this.cleanPetFromLoop(petId);
                }
            }
        }
    }

    public List<PetTypes> getAllPetTypes(){
        return PetTypes.PREDEFINED_PET_TYPES;
    }

    public Boolean generateANewPetForOwner(String ownerName, String petName, String petType) {
        if (this.petRepository.getPetsByPetOwner(ownerName).size() >= 3) {
            return false;
        } else {
            Pet newPet = new Pet(
                    petName,
                    ownerName,
                    PetTypes.getPetTypeByName(petType),
                    50,
                    50,
                    300,
                    390,
                    "right",
                    PetActions.IDLE.getValue(),
                    0
            );
            this.petRepository.save(newPet);
            return true;
        }
    }

    public Boolean abandonPet(String ownerName, int petId) {
        Pet pet = this.petRepository.findByPetId(petId);
        if (pet != null && pet.getPetOwner().equals(ownerName) && pet.getShared() < 1) {
            this.petRepository.removePetByPetId(petId);
            return true;
        } else {
            return false;
        }
    }

    public Boolean sharePet(String ownerName, int petId) {
        Pet pet = this.petRepository.findByPetId(petId);
        if (pet != null && pet.getPetOwner().equals(ownerName)) {
            pet.setShared(1);
            this.petRepository.save(pet);
            return true;
        }
        return false;
    }

    public Boolean unsharePet(String ownerName, int petId) {
        Pet pet = this.petRepository.findByPetId(petId);
        if (pet != null && pet.getPetOwner().equals(ownerName)) {
            pet.setShared(0);
            this.petRepository.save(pet);
            this.cleanPetFromLoop(petId);

            messagingTemplate.convertAndSend("/topic/shared/pet/" + petId, "CLOSE");
            return true;
        }
        return false;
    }

    private void cleanPetFromLoop(int petId) {
        movementCounters.remove(petId);
        this.petFoodService.getAllPetsFoods().remove(petId);
        this.petBehaviorService.getAllPetActions().remove(petId);
        unregisterPet(petId);
    }
}

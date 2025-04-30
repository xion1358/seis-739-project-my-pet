package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Food;
import com.mypetserver.mypetserver.entities.Pet;
import com.mypetserver.mypetserver.models.PetActions;
import com.mypetserver.mypetserver.models.PetBehavior;
import com.mypetserver.mypetserver.models.PetData;
import com.mypetserver.mypetserver.repository.FoodRepository;
import com.mypetserver.mypetserver.repository.PetRepository;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Getter
@Service
public class PetBehaviorService {
    private static final Logger logger = LoggerFactory.getLogger(PetBehaviorService.class);

    public static final int PET_EATING_TIME = 4000;
    public static final int PET_MOVING_TIME = 6000;

    private final Random random = new Random();
    private final Map<Integer, PetBehavior> allPetActions = new HashMap<>();
    private final PetFoodService petFoodService;
    private final FoodRepository foodRepository;
    private final PetRepository petRepository;

    public PetBehaviorService(PetFoodService petFoodService,
                              FoodRepository foodRepository,
                              PetRepository petRepository) {
        this.petFoodService = petFoodService;
        this.foodRepository = foodRepository;
        this.petRepository = petRepository;
    }

    @Transactional
    public PetData updatePetBehavior(Pet pet) {
        try {
            PetBehavior petBehavior = this.getPetBehavior(pet);

            List<Food> petFood = this.petFoodService.getAllPetsFoods().getOrDefault(pet.getPetId(), new ArrayList<>());

            if (petBehavior != null && petBehavior.getTimeToNextActionInMillis() - System.currentTimeMillis() < 0) {
                PetBehavior generatedBehavior = this.generateBehavior(pet, petFood);

                petRepository.save(pet);
                return new PetData(pet, petFood, generatedBehavior.getPetAction().getValue(),
                        generatedBehavior.getTimeToNextActionInMillis()-System.currentTimeMillis());
            } else {
                PetBehavior noBehavior = new PetBehavior(0, PetActions.IDLE);
                return new PetData(pet, petFood, noBehavior.getPetAction().getValue(), 0);
            }
        } catch (Exception e) {
            logger.error("Exception during updatePetBehavior for pet {}", pet.getPetId(), e);
            throw e;
        }

    }

    private PetBehavior getPetBehavior(Pet pet) {
        PetBehavior petBehavior = this.allPetActions.get(pet.getPetId());
        return Objects.requireNonNullElseGet(petBehavior, () ->
                new PetBehavior(0, PetActions.fromValue(pet.getPetAction())));
    }

    private PetBehavior generateBehavior(Pet pet, List<Food> petFood) {
        PetBehavior petBehavior;
        if (pet.getPetAction().equals(PetActions.FORAGING.getValue())) {
            petBehavior =  handleEating(pet, petFood);
        } else if ((pet.getPetHungerLevel() <= 50 || shouldPetForage(pet.getPetHungerLevel())) && !petFood.isEmpty()) {
            petBehavior =  handleForaging(pet, petFood);
        } else {
            petBehavior =  handleWandering(pet);
        }

        this.allPetActions.put(pet.getPetId(), petBehavior);
        pet.setPetAction(petBehavior.getPetAction().getValue());
        return petBehavior;
    }

    private PetBehavior handleEating(Pet pet, List<Food> petFood) {
        Food food = findClosestFood(pet, petFood);
        if (food != null) {
            pet.setPetHungerLevel(Math.min(100, pet.getPetHungerLevel() + food.getFoodType().getFeedHunger()));
            pet.setPetLoveLevel(Math.min(100, pet.getPetLoveLevel() + food.getFoodType().getFeedLove()));
            this.foodRepository.removeFoodByFoodId(food.getFoodId());
            petFood.remove(food);
            this.petFoodService.getAllPetsFoods().put(pet.getPetId(), petFood);
        }
        return new PetBehavior(System.currentTimeMillis() + PET_EATING_TIME, PetActions.EATING);
    }

    private PetBehavior handleForaging(Pet pet, List<Food> petFood) {
        Food closestFood = findClosestFood(pet, petFood);
        int location = (closestFood != null) ? closestFood.getFoodXLocation() : pet.getPetXLocation();
        String direction = location > pet.getPetXLocation() ? "right" : "left";
        pet.setPetXLocation(location);
        pet.setPetDirection(direction);
        return new PetBehavior(System.currentTimeMillis() + PET_MOVING_TIME, PetActions.FORAGING);
    }

    private PetBehavior handleWandering(Pet pet) {
        boolean shouldRest = random.nextDouble() < 0.3;
        if (!shouldRest) {
            int newLocation = 100 + random.nextInt(500);
            String direction = newLocation > pet.getPetXLocation() ? "right" : "left";
            pet.setPetXLocation(newLocation);
            pet.setPetDirection(direction);
        }
        return new PetBehavior(System.currentTimeMillis() + PET_MOVING_TIME, PetActions.MOVING);
    }

    private boolean shouldPetForage(int hungerLevel) {
        return random.nextInt(101) < (100 - hungerLevel);
    }

    private Food findClosestFood(Pet pet, List<Food> foods) {
        return foods.stream()
                .min(Comparator.comparingInt(f -> Math.abs(pet.getPetXLocation() - f.getFoodXLocation())))
                .orElse(null);
    }


}


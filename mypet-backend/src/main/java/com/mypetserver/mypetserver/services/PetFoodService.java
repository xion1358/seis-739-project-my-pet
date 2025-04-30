package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Food;
import com.mypetserver.mypetserver.entities.FoodTypes;
import com.mypetserver.mypetserver.repository.FoodRepository;
import com.mypetserver.mypetserver.repository.PetRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class PetFoodService {
    private final PetRepository petRepository;
    private final FoodRepository foodRepository;

    private final Map<Integer, List<Food>> allPetsFoods = new ConcurrentHashMap<>();

    public PetFoodService(
            PetRepository petRepository,
            FoodRepository foodRepository) {
        this.petRepository = petRepository;
        this.foodRepository = foodRepository;
    }

    public Boolean createPetFood(int petId, String foodName) {
        if (petRepository.getPetByPetId(petId) != null && foodRepository.getFoodsByPetId(petId).size() < 3) {
            Food food = new Food(petId, FoodTypes.getFoodTypeByName(foodName), (int) (Math.random() * (700 - 100 + 1)) + 100, 400);
            foodRepository.save(food);
            allPetsFoods.put(petId, this.foodRepository.getFoodsByPetId(petId));
            return true;
        }
        return false;
    }

}

package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Food;
import com.mypetserver.mypetserver.entities.FoodTypes;
import com.mypetserver.mypetserver.entities.Pet;
import com.mypetserver.mypetserver.models.PetActions;
import com.mypetserver.mypetserver.models.PetBehavior;
import com.mypetserver.mypetserver.models.PetData;
import com.mypetserver.mypetserver.repository.FoodRepository;
import com.mypetserver.mypetserver.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static com.mypetserver.mypetserver.services.PetBehaviorService.PET_MOVING_TIME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PetBehaviorServiceTest {

    @Mock private PetFoodService petFoodService;
    @Mock private FoodRepository foodRepository;
    @Mock private PetRepository petRepository;

    @InjectMocks
    private PetBehaviorService petBehaviorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Pet createTestPet() {
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setPetHungerLevel(30);
        pet.setPetAffectionLevel(40);
        pet.setPetAction(PetActions.IDLE.getValue());
        pet.setPetXLocation(100);
        return pet;
    }

    private Food createTestFood(int foodId, int xLocation) {
        Food food = new Food();
        FoodTypes foodType = FoodTypes.getFoodTypeByName("kibble");
        food.setFoodType(foodType);
        food.setFoodId(foodId);
        food.setFoodXLocation(xLocation);
        return food;
    }

    @Test
    void testUpdatePetBehaviorWhenBehaviorExistsAndNotDue() {
        Pet pet = createTestPet();
        Map<Integer, List<Food>> petFoods = new HashMap<>();
        petFoods.put(pet.getPetId(), new ArrayList<>());

        long futureTime = System.currentTimeMillis() + PET_MOVING_TIME;
        PetBehavior petBehavior = new PetBehavior(futureTime, PetActions.IDLE);
        petBehaviorService.getAllPetActions().put(pet.getPetId(), petBehavior);

        when(petFoodService.getAllPetsFoods()).thenReturn(petFoods);

        PetData result = petBehaviorService.updatePetBehavior(pet);

        assertEquals(PetActions.IDLE.getValue(), result.getPetBehavior());
        assertEquals(0, result.getActionTime());

        verify(petRepository, never()).save(any());
    }


    @Test
    void testUpdatePetBehaviorWhenForagingAndFoodAvailable() {
        Pet pet = createTestPet();
        pet.setPetAction(PetActions.MOVING.getValue());

        Food food = createTestFood(101, 120);
        List<Food> foods = new ArrayList<>(Collections.singletonList(food));

        Map<Integer, List<Food>> allFoods = new HashMap<>();
        allFoods.put(pet.getPetId(), foods);

        when(petFoodService.getAllPetsFoods()).thenReturn(allFoods);

        petBehaviorService.getAllPetActions().put(pet.getPetId(),
                new com.mypetserver.mypetserver.models.PetBehavior(System.currentTimeMillis() - 1000, PetActions.MOVING));

        PetData result = petBehaviorService.updatePetBehavior(pet);

        assertEquals(PetActions.FORAGING.getValue(), result.getPetBehavior());
        assertEquals(pet.getPetXLocation(), food.getFoodXLocation());
        verify(petRepository).save(pet);
    }

    @Test
    void testUpdatePetBehaviorWhenEating() {
        Pet pet = createTestPet();
        pet.setPetAction(PetActions.FORAGING.getValue());

        Food food = createTestFood(201, 100);
        List<Food> foods = new ArrayList<>(Collections.singletonList(food));

        Map<Integer, List<Food>> allFoods = new HashMap<>();
        allFoods.put(pet.getPetId(), foods);

        when(petFoodService.getAllPetsFoods()).thenReturn(allFoods);

        // Force the behavior to trigger
        petBehaviorService.getAllPetActions().put(pet.getPetId(),
                new PetBehavior(System.currentTimeMillis() - 1000, PetActions.FORAGING));

        PetData result = petBehaviorService.updatePetBehavior(pet);

        assertEquals(PetActions.EATING.getValue(), result.getPetBehavior());
        verify(foodRepository).removeFoodByFoodId(food.getFoodId());
        verify(petRepository).save(pet);
    }

    @Test
    void testUpdatePetBehaviorWhenExceptionThrown() {
        Pet pet = createTestPet();
        when(petFoodService.getAllPetsFoods()).thenThrow(new RuntimeException("DB error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            petBehaviorService.updatePetBehavior(pet);
        });

        assertEquals("DB error", thrown.getMessage());
    }
}

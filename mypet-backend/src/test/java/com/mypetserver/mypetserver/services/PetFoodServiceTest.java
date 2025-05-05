package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Food;
import com.mypetserver.mypetserver.entities.Pet;
import com.mypetserver.mypetserver.repository.FoodRepository;
import com.mypetserver.mypetserver.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetFoodServiceTest {

    private PetRepository petRepository;
    private FoodRepository foodRepository;
    private PetFoodService petFoodService;

    @BeforeEach
    void setUp() {
        petRepository = mock(PetRepository.class);
        foodRepository = mock(FoodRepository.class);
        petFoodService = new PetFoodService(petRepository, foodRepository);
    }

    @Test
    void testCreatePetFoodSuccess() {
        int petId = 1;
        String foodName = "kibble";

        Pet mockPet = new Pet();
        mockPet.setPetId(petId);

        List<Food> currentFoods = new ArrayList<>();

        when(petRepository.findByPetId(petId)).thenReturn(mockPet);
        when(foodRepository.findFoodByPetIdForUpdate(petId)).thenReturn(currentFoods);
        when(foodRepository.findFoodByPetId(petId)).thenReturn(List.of(new Food()));

        boolean result = petFoodService.createPetFood(petId, foodName);

        assertTrue(result);
        verify(foodRepository).save(any(Food.class));
        assertEquals(1, petFoodService.getAllPetsFoods().get(petId).size());
    }

    @Test
    void testCreatePetFoodPetNotFound() {
        int petId = 2;
        when(petRepository.findByPetId(petId)).thenReturn(null);

        boolean result = petFoodService.createPetFood(petId, "kibble");

        assertFalse(result);
        verify(foodRepository, never()).save(any());
    }

    @Test
    void testCreatePetFoodTooManyFoods() {
        int petId = 3;
        Pet mockPet = new Pet();
        mockPet.setPetId(petId);

        List<Food> currentFoods = List.of(new Food(), new Food(), new Food());

        when(petRepository.findByPetId(petId)).thenReturn(mockPet);
        when(foodRepository.findFoodByPetIdForUpdate(petId)).thenReturn(currentFoods);

        boolean result = petFoodService.createPetFood(petId, "kibble");

        assertFalse(result);
        verify(foodRepository, never()).save(any());
    }

    @Test
    void testCreatePetFoodInvalidFoodName() {
        int petId = 4;
        Pet mockPet = new Pet();
        mockPet.setPetId(petId);

        when(petRepository.findByPetId(petId)).thenReturn(mockPet);
        when(foodRepository.findFoodByPetIdForUpdate(petId)).thenReturn(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () ->
                petFoodService.createPetFood(petId, "invalid-food"));
    }
}

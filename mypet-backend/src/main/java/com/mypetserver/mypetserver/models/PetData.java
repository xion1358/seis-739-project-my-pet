package com.mypetserver.mypetserver.models;

import com.mypetserver.mypetserver.entities.Food;
import com.mypetserver.mypetserver.entities.Pet;
import lombok.Getter;

import java.util.List;

/**
 * This class describes the pet data that is required for viewing a pet.
 * It is essential pet data for rendering a basic pet in the frontend.
 */
@Getter
public class PetData {
    private final Pet pet;
    private final List<Food> food;
    private final String petBehavior;
    private final long actionTime;

    public PetData(Pet pet, List<Food> food, String petBehavior, long actionTime) {
        this.pet = pet;
        this.food = food;
        this.petBehavior = petBehavior;
        this.actionTime = actionTime;
    }
}

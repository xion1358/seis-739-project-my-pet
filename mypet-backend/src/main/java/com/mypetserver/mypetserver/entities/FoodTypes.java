package com.mypetserver.mypetserver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * This class defines the food types entity. The below attributes are represented in the database.
 */
@Setter
@Getter
@Entity
@Table(name = "foodtypes")
public class FoodTypes {
    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "satiatehunger")
    private int satiateHunger;

    @Column(name = "satiateaffection")
    private int satiateAffection;

    public FoodTypes() {}

    private FoodTypes(String name, int satiateHunger, int satiateAffection) {
        this.name = name;
        this.satiateHunger = satiateHunger;
        this.satiateAffection = satiateAffection;
    }

    public static final List<FoodTypes> PREDEFINED_FOOD_TYPES = List.of(
            new FoodTypes("kibble", 20, 3),
            new FoodTypes("nutritional-meal", 10, 10)
    );

    public static FoodTypes getFoodTypeByName(String name) {
        return PREDEFINED_FOOD_TYPES.stream()
                .filter(foodType -> foodType.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Food type not found"));
    }
}

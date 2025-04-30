package com.mypetserver.mypetserver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "foodtypes")
public class FoodTypes {
    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "feedhunger")
    private int feedHunger;

    @Column(name = "feedlove")
    private int feedLove;

    public FoodTypes() {}

    private FoodTypes(String name, int feedHunger, int feedLove) {
        this.name = name;
        this.feedHunger = feedHunger;
        this.feedLove = feedLove;
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

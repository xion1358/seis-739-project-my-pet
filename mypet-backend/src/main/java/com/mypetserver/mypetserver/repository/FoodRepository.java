package com.mypetserver.mypetserver.repository;

import com.mypetserver.mypetserver.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Integer> {

    List<Food> getFoodsByPetId(@Param("PetId") int petId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Food f WHERE f.foodId = :foodId")
    void removeFoodByFoodId(@Param("foodId") int foodId);
}

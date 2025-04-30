package com.mypetserver.mypetserver.repository;

import com.mypetserver.mypetserver.entities.Food;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Integer> {

    List<Food> findFoodByPetId(@Param("PetId") int petId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query("SELECT f FROM Food f WHERE f.petId = :petId")
    List<Food> findFoodByPetIdForUpdate(@Param("petId") int petId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Food f WHERE f.foodId = :foodId")
    void removeFoodByFoodId(@Param("foodId") int foodId);
}

package com.mypetserver.mypetserver.repository;

import com.mypetserver.mypetserver.entities.Pet;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Integer> {

    @Query("SELECT p FROM Pet p WHERE p.petId = :petId")
    Pet findByPetId(@Param("petId") int petId);

    @Query("SELECT p FROM Pet p WHERE p.petOwner = :petOwner")
    List<Pet> getPetsByPetOwner(@Param("petOwner") String petOwner);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pet p WHERE p.petId = :petId")
    Pet findByPetIdForUpdate(@Param("petId") int petId);

    @Modifying
    @Transactional
    @Query("UPDATE Pet p SET p.petAffectionLevel = CASE WHEN p.petAffectionLevel <= 90 THEN p.petAffectionLevel + 10 ELSE 100 END WHERE p.petId = :petId")
    void petAPet(@Param("petId") int petId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pet p WHERE p.petId = :petId")
    void removePetByPetId(@Param("petId") int petId);
}

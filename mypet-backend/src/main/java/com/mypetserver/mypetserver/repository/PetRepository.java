package com.mypetserver.mypetserver.repository;

import com.mypetserver.mypetserver.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Integer> {
    Pet getPetByPetId(@Param("PetId") int petId);

    List<Pet> getPetsByPetOwner(String petOwner);

}

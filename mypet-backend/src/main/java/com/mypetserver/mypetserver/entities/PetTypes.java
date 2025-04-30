package com.mypetserver.mypetserver.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "pettypes")
public class PetTypes {
    @Id
    @Column(name = "name")
    private String name;

    public PetTypes() {}

    public PetTypes(String petTypeName) {
        this.name = petTypeName;
    }

    public static final List<PetTypes> PREDEFINED_PET_TYPES = List.of(
            new PetTypes("cat"),
            new PetTypes("dog")
    );

    public static PetTypes getPetTypeByName(String petTypeName) {
        return PREDEFINED_PET_TYPES.stream()
                .filter(petType -> petType.getName().equalsIgnoreCase(petTypeName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pet type not found"));
    }

}

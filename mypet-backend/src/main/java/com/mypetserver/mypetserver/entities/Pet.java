package com.mypetserver.mypetserver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * This class defines the owner interfacing entity for Pets in a database repository
 */
@Getter
@Setter
@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @Column(name = "petid")
    private int petId;

    @Column(name = "petname")
    private String petName;

    @Column(name = "petowner")
    private String petOwner;

    @Column(name = "pettype")
    private String petType;

    @Column(name = "petaffectionlevel")
    private int petAffectionLevel;

    @Column(name = "pethungerlevel")
    private int petHungerLevel;

    @Column(name = "petxlocation")
    private int petXLocation;

    @Column(name = "petylocation")
    private int petYLocation;

    @Column(name = "petdirection")
    private String petDirection;

    @Column(name = "petaction")
    private String petAction;

    public Pet(int petId, String petName, String petOwner, String petType,
               int petAffectionLevel, int petHunger, int x, int y, String direction,
               String petAction) {
        this.petId = petId;
        this.petName = petName;
        this.petOwner = petOwner;
        this.petType = petType;
        this.petAffectionLevel = petAffectionLevel;
        this.petHungerLevel = petHunger;
        this.petXLocation = x;
        this.petYLocation = y;
        this.petDirection = direction;
        this.petAction = petAction;
    }

    public Pet() {
    }
}

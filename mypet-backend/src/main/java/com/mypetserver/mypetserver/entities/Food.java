package com.mypetserver.mypetserver.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * This class defines the food entity. The below attributes are represented in the database.
 */
@Getter
@Setter
@Entity
@Table(name = "food")
public class Food {
    @Id
    @Column(name = "foodid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int foodId;

    @Column(name = "petid")
    private int petId;

    @ManyToOne
    @JoinColumn(name = "foodtype", referencedColumnName = "name")
    private FoodTypes foodType;

    @Column(name = "foodxlocation")
    private int foodXLocation;

    @Column(name = "foodylocation")
    private int foodYLocation;

    @Column(name = "createdtimestamp")
    private Timestamp createdTimestamp;

    public Food() {
    }

    public Food(int petId, FoodTypes foodType, int foodXLocation, int foodYLocation) {
        this.petId = petId;
        this.foodType = foodType;
        this.foodXLocation = foodXLocation;
        this.foodYLocation = foodYLocation;
        this.createdTimestamp = new Timestamp(System.currentTimeMillis());
    }

}

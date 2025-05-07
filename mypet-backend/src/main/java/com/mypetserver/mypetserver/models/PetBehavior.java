package com.mypetserver.mypetserver.models;

import lombok.Getter;
import lombok.Setter;

/**
 * This class describes the pet behavior object. It represents what a pet is currently doing, and if it is time
 * for the game loop the regenerate a new action in the form of timeToNextActionInMillis.
 */
@Getter
@Setter
public class PetBehavior {
    private long timeToNextActionInMillis;
    private PetActions petAction;

    public PetBehavior(long timeToNextActionInMillis, PetActions petAction) {
        this.timeToNextActionInMillis = timeToNextActionInMillis;
        this.petAction = petAction;
    }
}

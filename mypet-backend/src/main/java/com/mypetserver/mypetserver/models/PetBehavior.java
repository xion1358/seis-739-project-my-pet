package com.mypetserver.mypetserver.models;

import lombok.Getter;
import lombok.Setter;

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

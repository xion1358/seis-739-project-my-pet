package com.mypetserver.mypetserver.models;

/**
 * This enum describes the possible pet actions available. Is used in pet action generation in the pet loop.
 */
public enum PetActions {
    MOVING("moving"),
    FORAGING("foraging"),
    EATING("eating"),
    IDLE("idle"),
    RESTING("resting");

    private final String value;

    PetActions(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PetActions fromValue(String value) {
        for (PetActions action : PetActions.values()) {
            if (action.getValue().equalsIgnoreCase(value)) {
                return action;
            }
        }
        return PetActions.RESTING;
    }

    @Override
    public String toString() {
        return value;
    }
}

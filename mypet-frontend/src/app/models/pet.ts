import { PetTypes } from "./pettypes";

export interface Pet {
    petId: number;
    petName: string;
    petOwner: string;
    petType: PetTypes;
    petAffectionLevel: number;
    petHungerLevel: number;
    petXLocation: number;
    petYLocation: number;
    petDirection: string;
    petAction: string;
    shared: number;
  }
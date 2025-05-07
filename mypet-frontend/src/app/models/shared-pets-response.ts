import { Pet } from "./pet";

export interface SharedPetsResponse {
    pets: Pet[];
    hasNext: boolean;
    hasPrevious: boolean;
  }
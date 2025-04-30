import { FoodTypes } from "./foottypes";

export interface Food {
    foodId: number;
    petId: number;
    foodType: FoodTypes;
    foodXLocation: number;
    foodYLocation: number;
    createTimeStamp: Date
  }
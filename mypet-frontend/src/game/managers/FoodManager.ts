import { Food } from "../../app/models/food";

export class FoodManager {
    petFood: Food[] = [];
    petFoodSprites: Map<number, Phaser.Physics.Arcade.Sprite> = new Map();

    constructor(){
    }

    updateFood(scene: Phaser.Scene, foodList: Food[], action: String, ground: Phaser.GameObjects.Rectangle): void {
        const removedFood = this.findRemovedFood(this.petFood, foodList);

        if (removedFood.length > 0 && action === "eating"){
            this.cleanRemovedFood(removedFood);
        }

        for(const food of foodList) {
            const foodGenerated = this.petFood.find(f => f.foodId === food.foodId);

            if (!foodGenerated) {
                const foodSprite = scene.physics.add.sprite(food.foodXLocation, 200, food.foodType.name);
                foodSprite.setCollideWorldBounds(true);
                scene.physics.add.collider(foodSprite, ground);

                this.petFoodSprites.set(food.foodId, foodSprite);
            }
        }
        this.petFood = foodList;
    }
    
    cleanRemovedFood(removedFood: Food[]) {
        for (const food of removedFood) {
            const sprite = this.petFoodSprites.get(food.foodId);
            if (sprite)
            {
                sprite.destroy();
                this.petFoodSprites.delete(food.foodId);
            }
        }
    }
    
    findRemovedFood(oldList: Food[], newList: Food[]): Food[] {
        const newIds = new Set(newList.map(f => f.foodId));
        return oldList.filter(f => !newIds.has(f.foodId));
    }
}
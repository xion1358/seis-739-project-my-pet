import { Pet } from "../../app/models/pet";

export class FoodButtonManager {
    foodTypes: string[] = ['kibble', 'nutritional-meal'];
    foodButtons: Phaser.GameObjects.Sprite[] = [];

    constructor(
        scene: Phaser.Scene, 
        hoverText: Phaser.GameObjects.Text,
        petData: Pet,
        createPetFood: any
    ) { 
        this.foodTypes.forEach((type, i) => {
            const foodButton = scene.add.sprite(756 - ((i + 1) * 62), 100, type + '-button')
            .setInteractive({useHandCursor: true})
            .setAlpha(0)
            .on('pointerdown', () => {
                this.hideFoodButtons(scene);
                createPetFood(petData.petId, type);
                foodButton.setScale(1);
            })
            .on('pointerover', () => {
                hoverText.setText(type);
                hoverText.setOrigin(.5, 1);
                hoverText.setPosition(Math.round(foodButton.x), Math.round(foodButton.y + foodButton.height - hoverText.height + 10));
                hoverText.setVisible(true);
                foodButton.setScale(1.2);
            })
            .on('pointerout', () => {
                hoverText.setVisible(false);
                foodButton.setScale(1);
            });

            this.foodButtons.push(foodButton);
        });

        const foodButton = scene.add.sprite(756, 100, 'food-button')
            .setInteractive({ useHandCursor: true })
            .on('pointerdown', () => {
                foodButton.setTint(0x999999);
                foodButton.setScale(0.95);
                this.toggleFoodButtons(scene);
            })
            .on('pointerup', () => {
                foodButton.clearTint();
                foodButton.setScale(1);
            })
            .on('pointerover', () => {
                hoverText.setText("Feed Pet");
                hoverText.setOrigin(.5, 1);
                hoverText.setPosition(Math.round(foodButton.x), Math.round(foodButton.y + foodButton.height - hoverText.height + 10));
                hoverText.setVisible(true);
                foodButton.setScale(1.2);
            })
            .on('pointerout', () => {
                hoverText.setVisible(false);
                foodButton.clearTint();
                foodButton.setScale(1);
            });
    }

    toggleFoodButtons(scene: Phaser.Scene) {
        const buttonsAreVisible = this.foodButtons[0].alpha > 0;

        this.foodButtons.forEach( button => {
            scene.tweens.add({
                targets: button,
                alpha: buttonsAreVisible ? 0 : 1,
                duration: 300,
                ease: 'Power2'
            });
        });
    }

    showFoodButtons() {
        this.foodButtons.forEach(button => {
            button.setVisible(true);
        });
    }

    hideFoodButtons(scene: Phaser.Scene) {
    const buttonsAreVisible = this.foodButtons[0].alpha > 0;
    this.foodButtons.forEach( 
        button => {
            scene.tweens.add({
                targets: button,
                alpha: buttonsAreVisible ? 0 : 1,
                duration: 300,
                ease: 'Power2'
            });
        });
    }


}
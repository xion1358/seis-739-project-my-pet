import { GameObjects, Scene } from 'phaser';
import { EventBus } from '../EventBus';

import { Pet } from '../../app/models/pet';
import { Food } from '../../app/models/food';

export class MyPet extends Scene
{
    // Environments
    ground: GameObjects.Rectangle;
    GROUND_HEIGHT: number = 465;
    GROUND_THICKNESS: number = 40;
    loadingText: GameObjects.Text;
    hungerBarContainer: GameObjects.Rectangle;
    hungerBarFill: GameObjects.Rectangle;
    hungerBarText: GameObjects.Text;
    hoverText: GameObjects.Text;

    // Sprite
    petFood: Food[] = [];
    petFoodSprites: Map<number, Phaser.Physics.Arcade.Sprite> = new Map();
    petData: Pet;
    background: GameObjects.Image;
    petBodySprite: Phaser.Physics.Arcade.Sprite;
    petEyesSprite: GameObjects.Sprite;
    petContainer: Phaser.GameObjects.Container;
    foodTypes: string[] =  ['kibble', 'nutritional-meal'];
    foodButtons: Phaser.GameObjects.Sprite[] = [];

    // Game mechanics
    petTween: Phaser.Tweens.Tween;
    petBlinking: any;
    createPetFood: any;

    constructor()
    {
        super('MyPet');
    }

    init(data: any) {
        this.createPetFood = data.createPetFood;
    }

    create()
    {
        // Environment setup
        this.ground = this.add.rectangle(0, this.GROUND_HEIGHT, 800, this.GROUND_THICKNESS, 0x000000, 0); // transparent
        this.ground.setOrigin(0, 0);
        this.physics.add.existing(this.ground, true);

        // Background
        this.background = this.add.image(400, 300, 'loading-background');
        this.loadingText = this.add.text(400, 300, 'Loading your pet...', {
            fontSize: '24px',
            color: '#ffffff'
        }).setOrigin(0.5);

        this.events.on('shutdown', this.cleanup, this);
        this.events.on('destroy', this.cleanup, this);

        // Send event for scene ready
        EventBus.emit('current-scene-ready', this);
    }

    // Main pet update call
    updatePet(petData: Pet, foodList: Food[], action: String, actionTime: number): void {
        this.petData = petData;

        if (!this.petContainer)
        {
            this.createPet(petData);
        } else {
            if (action !== "idle") {
                this.petContainer.scaleX = this.shouldFaceLeft ? -1 : 1;
                if (this.petTween) {
                    this.petTween.stop();
                }

                this.petTween = this.tweens.add({
                    targets: this.petContainer,
                    x: petData.petXLocation,
                    duration: actionTime - 1000,
                    ease: 'Sine.easeInOut',
                    onComplete: () =>
                    {
                        //console.log("Tween complete");
                    }
                });
                this.scheduleBlinking(petData);
            }
        }

        this.updateFood(foodList, action);
        this.updateHungerBar(petData);
    }

    updateFood(foodList: Food[], action: String): void {
        const removedFood = this.findRemovedFood(this.petFood, foodList);

        if (removedFood.length > 0 && action === "eating"){
            this.cleanRemovedFood(removedFood);
        }

        for(const food of foodList) {
            const foodGenerated = this.petFood.find(f => f.foodId === food.foodId);

            if (!foodGenerated) {
                const foodSprite = this.physics.add.sprite(food.foodXLocation, 200, food.foodType.name);
                foodSprite.setCollideWorldBounds(true);
                this.physics.add.collider(foodSprite, this.ground);

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

    hideFoodButtons() {
        const buttonsAreVisible = this.foodButtons[0].alpha > 0;
        this.foodButtons.forEach( 
            button => {
                this.tweens.add({
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

    toggleFoodButtons() {
        const buttonsAreVisible = this.foodButtons[0].alpha > 0;

        this.foodButtons.forEach( button => {
            this.tweens.add({
                targets: button,
                alpha: buttonsAreVisible ? 0 : 1,
                duration: 300,
                ease: 'Power2'
            });
        });
        
    }

    // One time call to create initial pet
    createPet(petData: Pet) {

        this.hoverText = this.add.text(0, 0, '', {
            font: '14px Poppins',
            color: '#000000',
            padding: { x: 6, y: 4 }
        }).setDepth(10).setVisible(false).setResolution(2);


        this.foodTypes.forEach((type, i) => {
            const foodButton = this.add.sprite(756 - ((i + 1) * 62), 100, type + '-button')
            .setInteractive({useHandCursor: true})
            .setAlpha(0)
            .on('pointerdown', () => {
                this.hideFoodButtons();
                this.createPetFood(petData.petId, type);
                foodButton.setScale(1);
            })
            .on('pointerover', () => {
                this.hoverText.setText(type);
                this.hoverText.setOrigin(.5, 1);
                this.hoverText.setPosition(Math.round(foodButton.x), Math.round(foodButton.y + foodButton.height - this.hoverText.height + 10));
                this.hoverText.setVisible(true);
                foodButton.setScale(1.2);
            })
            .on('pointerout', () => {
                this.hoverText.setVisible(false);
                foodButton.setScale(1);
            });

            this.foodButtons.push(foodButton);
        });


        const foodButton = this.add.sprite(756, 100, 'food-button')
            .setInteractive({ useHandCursor: true })
            .on('pointerdown', () => {
                foodButton.setTint(0x999999);
                foodButton.setScale(0.95);
                this.toggleFoodButtons();
            })
            .on('pointerup', () => {
                foodButton.clearTint();
                foodButton.setScale(1);
            })
            .on('pointerover', () => {
                this.hoverText.setText("Feed Pet");
                this.hoverText.setOrigin(.5, 1);
                this.hoverText.setPosition(Math.round(foodButton.x), Math.round(foodButton.y + foodButton.height - this.hoverText.height + 10));
                this.hoverText.setVisible(true);
                foodButton.setScale(1.2);
            })
            .on('pointerout', () => {
                this.hoverText.setVisible(false);
                foodButton.clearTint();
                foodButton.setScale(1);
            });

        // Set sprites
        this.petBodySprite = this.physics.add.sprite(0, 0, petData.petType.toLowerCase() + '-body');
        this.petEyesSprite = this.add.sprite(0, 0, petData.petType.toLowerCase() + '-eyes');
        this.petContainer = this.add.container(petData.petXLocation, this.GROUND_HEIGHT - this.petBodySprite.height/2, [ // petData.petYLocation
            this.petBodySprite,
            this.petEyesSprite
        ]);
        this.petContainer.scaleX = this.shouldFaceLeft ? -1 : 1;
        this.petBodySprite.setCollideWorldBounds(true);
        this.physics.add.collider(this.petBodySprite, this.ground);
        this.petContainer.setDepth(99);
        
        // Set Pet Stats
        this.setupHungerBar(petData);

        // Create and set animations
        this.anims.create({
            key: 'blink',
            frames: this.anims.generateFrameNames(petData.petType.toLowerCase() + '-eyes', { start: 0, end: 1 }),
            frameRate: 6,
            repeat: 0,
            yoyo: true
        });
        this.scheduleBlinking(petData);

        // Clean up from loading screen
        if (this.loadingText) {
            this.background.setTexture("background");
            this.loadingText.destroy();
        }
    }

    // Animation actions
    scheduleBlinking(petData: Pet): void
    {
        const blinkInterval = 2000 + (petData.petXLocation % 2000);
        const blinkOffset = petData.petXLocation % blinkInterval;

        if (this.petBlinking){
            clearTimeout(this.petBlinking);
        }

        const scheduleBlink = () => {
            if (this.petEyesSprite) {
                this.petEyesSprite.anims.play('blink', true);
            }
            this.petBlinking = setTimeout(scheduleBlink, blinkInterval);
        };

        this.petBlinking = setTimeout(scheduleBlink, blinkOffset);
    }

    // Pet Bars
    setupHungerBar(petData: Pet) {
        const barWidth = 234;
        const barHeight = 16;
        const padding = 20;
    
        const x = 800 - padding - (barWidth / 2);
        const y = padding + (barHeight / 2);
    
        this.hungerBarContainer = this.add.rectangle(x, y, barWidth, barHeight).setStrokeStyle(1, 0xffffff);
        this.hungerBarFill = this.add.rectangle(x - (barWidth / 2) + 2, y, 4, barHeight - 4, 0xffffff);
        this.hungerBarFill.setOrigin(0, 0.5);

        this.hungerBarText = this.add.text(x, y, 'Hunger', {
            fontSize: '12px',
            color: '#000000',
            fontFamily: 'Arial'
        }).setOrigin(0.5);

        this.updateHungerBar(petData);
    }
    
    updateHungerBar(petData: Pet) {
        const hunger = petData.petHungerLevel;
        const fillWidth = 230;
        this.hungerBarFill.width = (hunger / 100) * fillWidth;
        this.evaulateHungerColor(hunger);        
    }

    evaulateHungerColor(hunger: number) {
        if (hunger >= 51) {
            this.hungerBarFill.fillColor = 0x00ff00;
        } else if (hunger >= 21) {
            this.hungerBarFill.fillColor = 0xffff00;
        } else {
            this.hungerBarFill.fillColor = 0xff0000;
        }
    }

    // Misc.
    get shouldFaceLeft(): boolean {
        return this.petData?.petDirection.toLowerCase() === 'left';
    }

    // Clean up
    cleanup() {
        if (this.petBlinking) {
            clearTimeout(this.petBlinking);
            this.petBlinking = null;
        }
    }

}

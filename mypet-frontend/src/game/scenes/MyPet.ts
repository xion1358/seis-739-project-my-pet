import { GameObjects, Scene } from 'phaser';
import { EventBus } from '../EventBus';

import { Pet } from '../../app/models/pet';
import { Food } from '../../app/models/food';
import { HungerBar } from '../ui/HungerBar';
import { PetFactory } from '../pet/PetFactory';
import { FoodButtonManager } from '../managers/FoodButtonManager';
import { FoodManager } from '../managers/FoodManager';
import { PetBehaviorManager } from '../managers/PetBehaviorManager';
import { AffectionBar } from '../ui/AffectionBar';

export class MyPet extends Scene
{
    // Constants and data
    GROUND_HEIGHT: number = 465;
    GROUND_THICKNESS: number = 40;
    petData: Pet;

    // Managers
    petBehaviorManager: PetBehaviorManager;
    foodButtonManager: FoodButtonManager;
    foodManager: FoodManager;

    // Sprites and UI elements
    ground: GameObjects.Rectangle;
    loadingText: GameObjects.Text;
    background: GameObjects.Image;
    petBodySprite: Phaser.Physics.Arcade.Sprite;
    petEyesSprite: GameObjects.Sprite;
    petContainer: Phaser.GameObjects.Container;
    hungerBar: HungerBar;
    affectionBar: AffectionBar;
    hoverText: GameObjects.Text;

    // Game mechanics/outside method calls (IOC)
    createPetFood: any;
    petAPet: any;
    blinkTimer: Phaser.Time.TimerEvent;

    constructor()
    {
        super('MyPet');
    }

    // Initialize IOC method calls
    init(data: any) {
        this.createPetFood = data.createPetFood;
        this.petAPet = data.petAPet;
    }

    // Called when scene is first created
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

        this.events.once('shutdown', this.cleanup, this);
        this.events.once('destroy', this.cleanup, this);

        // Send event for scene ready
        EventBus.emit('current-scene-ready', this);
    }

    // Main pet update call
    updatePet(petData: Pet, foodList: Food[], action: String, actionTime: number): void {
        this.petData = petData;

        if (!this.petContainer)
        {
            this.generatePet(petData);
        } else {
            this.petBehaviorManager.updatePetBehavior(this, petData, this.petContainer, action, actionTime);
        }

        this.foodManager.updateFood(this, foodList, action, this.ground);
        this.hungerBar.updateHungerBar(petData.petHungerLevel);
        this.affectionBar.updateAffectionBar(petData.petAffectionLevel);
    }

    // One time call to generate initial pet and related items
    generatePet(petData: Pet) {

        // Set up UI items
        this.hungerBar = new HungerBar(this);
        this.affectionBar = new AffectionBar(this);

        this.hoverText = this.add.text(0, 0, '', {
            font: '14px Poppins',
            color: '#000000',
            padding: { x: 6, y: 4 }
        }).setDepth(10).setVisible(false).setResolution(2);

        // Set up managers
        this.petBehaviorManager = new PetBehaviorManager();
        this.foodManager = new FoodManager();
        this.foodButtonManager = new FoodButtonManager(this, this.hoverText, petData, this.createPetFood);

        // Set sprites
        const { container, bodySprite, eyesSprite } = PetFactory.createPet(this, petData, this.petAPet);
        this.petContainer = container;
        this.petBodySprite = bodySprite;
        this.petEyesSprite = eyesSprite;

        // Sprite physics
        this.physics.add.collider(this.petBodySprite, this.ground);

        // Clean up from loading screen
        if (this.loadingText) {
            this.background.setTexture("background");
            this.loadingText.destroy();
        }

        // Set up blinking
        this.scheduleBlinking(petData.petXLocation, this.petEyesSprite);
    }

    scheduleBlinking(petXLocation: number, petEyesSprite: Phaser.GameObjects.Sprite): void {
        const blinkInterval = 2000 + (petXLocation % 2000);
        const blinkOffset = petXLocation % blinkInterval;
    
        this.time.delayedCall(blinkOffset, () => {
            this.blinkTimer = this.time.addEvent({
                delay: blinkInterval,
                callback: () => {
                    if (petEyesSprite && petEyesSprite.anims) {
                        petEyesSprite.anims.play('blink', true);
                    }
                },
                callbackScope: this,
                loop: true
            });
        });
    }

    // Clean up
    cleanup() {
        // Cleanup managers
        if (this.petBehaviorManager) {
            this.petBehaviorManager.destroy();
        }

        // Cleanup assets
        if (this.ground) {
            //console.log("Destroying ground");
            this.ground.destroy();
        }

        if (this.loadingText) {
            this.loadingText.destroy();
        }

        if (this.background) {
            this.background.destroy();
        }
    
        if (this.petContainer) {
            this.petContainer.destroy(true);
        }
    
        if (this.petBodySprite) {
            this.petBodySprite.destroy(true);
        }
    
        if (this.petEyesSprite) {
            this.petEyesSprite.destroy(true);
        }
    
        if (this.hungerBar) {
            this.hungerBar.destroy();
        }
    
        if (this.affectionBar) {
            this.affectionBar.destroy();
        }
    
        if (this.hoverText) {
            this.hoverText.destroy();
        }

        // Clean up misc.
        if (this.blinkTimer) {
            this.blinkTimer.destroy();
        }
    
        // Remove event listeners
        this.events.off('shutdown', this.cleanup, this);
        this.events.off('destroy', this.cleanup, this);
    }
}

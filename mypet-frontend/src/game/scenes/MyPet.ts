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
    // Environments
    ground: GameObjects.Rectangle;
    GROUND_HEIGHT: number = 465;
    GROUND_THICKNESS: number = 40;
    loadingText: GameObjects.Text;
    hungerBar: HungerBar;
    affectionBar: AffectionBar;
    hoverText: GameObjects.Text;

    // Managers
    petBehaviorManager: PetBehaviorManager;
    foodButtonManager: FoodButtonManager;
    foodManager: FoodManager;

    // Sprite
    petData: Pet;
    background: GameObjects.Image;
    petBodySprite: Phaser.Physics.Arcade.Sprite;
    petEyesSprite: GameObjects.Sprite;
    petContainer: Phaser.GameObjects.Container;

    // Game mechanics
    createPetFood: any;
    petAPet: any;

    constructor()
    {
        super('MyPet');
    }

    init(data: any) {
        this.createPetFood = data.createPetFood;
        this.petAPet = data.petAPet;
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
            this.generatePet(petData);
        } else {
            this.petBehaviorManager.updatePetBehavior(this, petData, this.petEyesSprite, this.petContainer, action, actionTime);
        }

        this.foodManager.updateFood(this, foodList, action, this.ground);
        this.hungerBar.updateHungerBar(petData.petHungerLevel);
        this.affectionBar.updateAffectionBar(petData.petLoveLevel);
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
    }

    // Clean up
    cleanup() {
        if (this.petBehaviorManager) {
            this.petBehaviorManager.cleanup();
        }
    }
}

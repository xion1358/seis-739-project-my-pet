import { GameObjects, Scene } from 'phaser';
import { EventBus } from '../EventBus';

import { Client } from '@stomp/stompjs'

import { Pet } from '../../app/models/pet';

export class MyPet extends Scene
{
    petData: Pet;
    stompClient: Client;
    background: GameObjects.Image;
    petBodySprite: GameObjects.Sprite;
    petEyesSprite: GameObjects.Sprite;
    petContainer: Phaser.GameObjects.Container;
    petTween: Phaser.Tweens.Tween;
    petBlinking: any;

    constructor()
    {
        super('MyPet');
    }

    create()
    {
        // Background
        this.background = this.add.image(400, 300, 'background');

        this.events.on('shutdown', this.cleanup, this);
        this.events.on('destroy', this.cleanup, this);

        // Send event for scene ready
        EventBus.emit('current-scene-ready', this);
    }

    updatePetData(petData: Pet): void {
        this.petData = petData;

        if (!this.petContainer) {
            this.createPet(petData);
        } else {
            this.petContainer.scaleX = this.shouldFaceLeft ? -1 : 1;

            if (this.petTween) {
                this.petTween.stop();
            }

            this.petTween = this.tweens.add({
                targets: this.petContainer,
                x: petData.petXLocation,
                duration: 4000,
                ease: 'Sine.easeInOut',
                onComplete: () =>
                {
                    //console.log("Tween complete");
                }
            });

            this.scheduleBlinking(petData);
        }
    }

    createPet(petData: Pet) {

        // Set sprites
        this.petBodySprite = this.add.sprite(0, 0, petData.petType.toLowerCase() + '-body');
        this.petEyesSprite = this.add.sprite(0, 0, petData.petType.toLowerCase() + '-eyes');
        this.petContainer = this.add.container(petData.petXLocation, petData.petYLocation, [
            this.petBodySprite,
            this.petEyesSprite
        ]);
        this.petContainer.scaleX = this.shouldFaceLeft ? -1 : 1;
        

        this.anims.create({
            key: 'blink',
            frames: this.anims.generateFrameNames(petData.petType.toLowerCase() + '-eyes', { start: 0, end: 1 }),
            frameRate: 6,
            repeat: 0,
            yoyo: true
        });
        this.scheduleBlinking(petData);
    }

    get shouldFaceLeft(): boolean {
        return this.petData?.petDirection.toLowerCase() === 'left';
    }

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

    cleanup() {
        if (this.petBlinking) {
            clearTimeout(this.petBlinking);
            this.petBlinking = null;
        }
    }

}

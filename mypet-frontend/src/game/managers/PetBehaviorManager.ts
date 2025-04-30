import { Pet } from "../../app/models/pet";

export class PetBehaviorManager {
    petBlinking: any;
    petTween: Phaser.Tweens.Tween;

    constructor() {}

    updatePetBehavior(
        scene: Phaser.Scene,
         petData: Pet,
         petEyesSprite: Phaser.GameObjects.Sprite,
         petContainer: Phaser.GameObjects.Container,
         action: String,
         actionTime: number
        ) {
        if (action !== "idle") {
            petContainer.scaleX = this.shouldFaceLeft(petData.petDirection) ? -1 : 1;
            if (this.petTween) {
                this.petTween.stop();
            }

            this.petTween = scene.tweens.add({
                targets: petContainer,
                x: petData.petXLocation,
                duration: actionTime - 1000,
                ease: 'Sine.easeInOut',
                onComplete: () =>
                {
                    //console.log("Tween complete");
                }
            });
            this.scheduleBlinking(petData.petXLocation, petEyesSprite);
        }
    }

    shouldFaceLeft(petDirection: String): boolean {
        return petDirection.toLowerCase() === 'left';
    }

    scheduleBlinking(petXLocation: number, petEyesSprite: Phaser.GameObjects.Sprite): void
    {
        const blinkInterval = 2000 + (petXLocation % 2000);
        const blinkOffset = petXLocation % blinkInterval;

        if (this.petBlinking){
            clearTimeout(this.petBlinking);
        }

        const scheduleBlink = () => {
            if (petEyesSprite) {
                petEyesSprite.anims.play('blink', true);
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
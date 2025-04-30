import { Pet } from "../../app/models/pet";

export class PetBehaviorManager {
    petTween: Phaser.Tweens.Tween | null;

    constructor() {}

    updatePetBehavior(
        scene: Phaser.Scene,
         petData: Pet,
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
        }
    }

    shouldFaceLeft(petDirection: String): boolean {
        return petDirection.toLowerCase() === 'left';
    }

    destroy(): void {
        if (this.petTween) {
            this.petTween.stop();
            this.petTween = null;
        }
    }
    
}
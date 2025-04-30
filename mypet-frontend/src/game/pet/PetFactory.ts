import { Scene, GameObjects, Physics } from 'phaser';
import { Pet } from '../../app/models/pet';

export class PetFactory {
  static createPet(scene: Scene, petData: Pet, onPetClick: (petId: number) => void): {
    container: GameObjects.Container,
    bodySprite: Physics.Arcade.Sprite,
    eyesSprite: GameObjects.Sprite
  } {
    const groundHeight = 465;
    const petType = petData.petType.name.toLowerCase();

    // Pet body
    const petBodySprite = scene.physics.add.sprite(0, 0, `${petType}-body`)
      .setInteractive({ useHandCursor: true })
      .on('pointerdown', () => onPetClick(petData.petId))
      .setCollideWorldBounds(true);

    // Pet eyes
    const petEyesSprite = scene.add.sprite(0, 0, `${petType}-eyes`);

    // Container
    const petContainer = scene.add.container(
      petData.petXLocation,
      groundHeight - petBodySprite.height / 2,
      [petBodySprite, petEyesSprite]
    );

    petContainer.setDepth(99);
    petContainer.scaleX = petData.petDirection.toLowerCase() === 'left' ? -1 : 1;

    // Blink animation
    scene.anims.create({
      key: 'blink',
      frames: scene.anims.generateFrameNames(`${petType}-eyes`, { start: 0, end: 1 }),
      frameRate: 6,
      repeat: 0,
      yoyo: true
    });

    return {
      container: petContainer,
      bodySprite: petBodySprite,
      eyesSprite: petEyesSprite
    };
  }
}

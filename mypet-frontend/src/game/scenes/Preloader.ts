import { Scene } from 'phaser';

export class Preloader extends Scene
{
    private sceneData: any;

    constructor ()
    {
        super('Preloader');
    }

    init (data: any)
    {
        this.sceneData = data;

        //  We loaded these images in our Boot Scene, so we can display it here
        this.add.image(300, 300, 'loading-background');
        this.add.image(380, 230, 'MyPetTitle').setOrigin(0.5).setScale(.3);

        //  A simple progress bar. This is the outline of the bar.
        this.add.rectangle(400, 300, 468, 32).setStrokeStyle(1, 0xffffff);

        //  This is the progress bar itself. It will increase in size from the left based on the % of progress.
        const bar = this.add.rectangle(400 - (468 / 2) + 2, 300, 4, 28, 0xffffff);

        //  Use the 'progress' event emitted by the LoaderPlugin to update the loading bar
        this.load.on('progress', (progress: number) => {

            //  Update the progress bar (our bar is 464px wide, so 100% = 464px)
            bar.width = 4 + (464 * progress);

        });
    }

    preload ()
    {
        //  Load the assets for the game - Replace with your own assets
        this.load.setPath('assets');

        // Load misc.
        // for (let i = 0; i < 1000; i++) { // Use this for testing loading bar
        //     this.load.image(`dummy${i}`, 'sprites/cat-body-sprite.png');
        // }

        // Load Pet Sprites
        this.load.spritesheet('cat-body', 'sprites/pets/cat-body-sprite.png', {
            frameWidth: 150,
            frameHeight: 150
        });
        this.load.spritesheet('cat-eyes', 'sprites/pets/cat-eyes-sprite.png', {
            frameWidth: 150,
            frameHeight: 150
        });

        this.load.spritesheet('dog-body', 'sprites/pets/dog-body-sprite.png', {
            frameWidth: 150,
            frameHeight: 150
        });
        this.load.spritesheet('dog-eyes', 'sprites/pets/dog-eyes-sprite.png', {
            frameWidth: 150,
            frameHeight: 150
        });

        // Load Food Sprites
        this.load.image('kibble', 'sprites/food/kibble-food.png');
        this.load.image('nutritional-meal', 'sprites/food/nutritional-meal-food.png');

        // Load Button Sprites
        this.load.image('food-button', 'sprites/buttons/food-button.png');
        this.load.image('kibble-button', 'sprites/buttons/kibble-button.png');
        this.load.image('nutritional-meal-button', 'sprites/buttons/nutritional-meal-button.png');

        this.load.on('complete', () => {
            this.textures.get('food-button').setFilter(Phaser.Textures.FilterMode.NEAREST);
            this.textures.get('kibble-button').setFilter(Phaser.Textures.FilterMode.NEAREST);
            this.textures.get('nutritional-meal-button').setFilter(Phaser.Textures.FilterMode.NEAREST);
        });
    }

    create ()
    {
        //  When all the assets have loaded, it's often worth creating global objects here that the rest of the game can use.
        //  For example, you can define global animations here, so we can use them in other scenes.

        //  Move to the Game. You could also swap this for a Scene Transition, such as a camera fade.
        this.scene.start('MyPet', this.sceneData);
    }
}

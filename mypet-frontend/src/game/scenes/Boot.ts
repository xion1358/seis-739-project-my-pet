import { Scene } from 'phaser';

export class Boot extends Scene
{
    constructor ()
    {
        super('Boot');
    }

    preload ()
    {
        //  The Boot Scene is typically used to load in any assets you require for your Preloader, such as a game logo or background.
        //  The smaller the file size of the assets, the better, as the Boot Scene itself has no preloader.

        this.load.image('MyPetTitle', 'assets/MyPetTitle.png');
        this.load.image('background', 'assets/MyPetBackground.png');
        this.load.image('loading-background', '/assets/bg.png');
    }

    create ()
    {
        this.scene.start('Preloader');
    }
}

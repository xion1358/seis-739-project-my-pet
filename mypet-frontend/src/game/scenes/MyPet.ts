import { GameObjects, Scene } from 'phaser';

import { EventBus } from '../EventBus';

export class MyPet extends Scene
{
    background: GameObjects.Image;
    logo: GameObjects.Image;
    title: GameObjects.Text;
    logoTween: Phaser.Tweens.Tween | null;

    constructor ()
    {
        super('MyPet');
    }

    create ()
    {
        this.background = this.add.image(400, 300, 'background');

        this.logo = this.add.image(400, 300, 'logo').setDepth(100).setDisplaySize(500, 400);

        EventBus.emit('current-scene-ready', this);
    }
}

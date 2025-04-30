import { Boot } from './scenes/Boot';
import { AUTO, Game } from 'phaser';
import { Preloader } from './scenes/Preloader';
import { MyPet } from './scenes/MyPet';

//  Find out more information about the Game Config at:
//  https://newdocs.phaser.io/docs/3.70.0/Phaser.Types.Core.GameConfig
const config: Phaser.Types.Core.GameConfig = {
    type: AUTO,
    parent: 'game-container',
    backgroundColor: '#D9D9D9',
    scene: [
        Boot,
        Preloader
    ],
    scale: {
        mode: Phaser.Scale.RESIZE,
        autoCenter: Phaser.Scale.CENTER_BOTH,
    },
    physics: {
        default: "arcade",
        arcade: {
            gravity: {x: 0, y: 500},
            debug: false
        }
    }
};

const StartGame = (parent: string, createFoodCallback: (petId: number, food: string) => void) => {
    const game = new Game({...config, parent});
    game.scene.add('MyPet', MyPet, false);
    game.scene.start('MyPet', {
        createPetFood: createFoodCallback
    });

    return game;

}

export default StartGame;

import { Component, Input, OnInit } from "@angular/core";
import Phaser from "phaser";
import StartGame from "./main";
import { EventBus } from "./EventBus";
import { MyPet } from "./scenes/MyPet";
import { PetService } from "../app/services/pet.service";
import { Pet } from "../app/models/pet";
import { Subscription } from "rxjs";
import { ActivatedRoute } from "@angular/router";

@Component({
    selector: 'phaser-game',
    template: '<div id="game-container"></div>',
    standalone: true,
})
export class PhaserGame implements OnInit
{

    scene: Phaser.Scene;
    game: Phaser.Game;
    messageSub: Subscription;
    _onSceneReady: (scene: Phaser.Scene) => void;
    initialPetData: Pet;

    constructor(
        private _petService: PetService,
        private _route: ActivatedRoute
    ) {}

    sceneCallback: (scene: Phaser.Scene) => void;

    ngOnInit()
    {
        this.viewPet();

        this.game = StartGame('game-container');

        this._onSceneReady = (scene: Phaser.Scene) => {
            this.scene = scene;

            if (scene instanceof MyPet){
                if (this.initialPetData) {
                    scene.updatePetData(this.initialPetData);
                }
                this.messageSub = this._petService.messages$.subscribe(petData => {
                    const pet = petData as Pet;
                    if (pet) {
                        scene.updatePetData(pet);
                        //console.log("Got pet: " + JSON.stringify(petData));
                    }
                });
            }

            if (this.sceneCallback)
            {
                this.sceneCallback(scene);
            }
        }

        EventBus.on('current-scene-ready', this._onSceneReady);
    }

    // Component unmounted
    ngOnDestroy()
    {
        if (this.game)
        {
            this.game.destroy(true);
        }
        if (this.messageSub) {
            this.messageSub.unsubscribe();
        }
        if (this._petService) {
            this._petService.unsubscribeFromViewingPet();
        }
        EventBus.off('current-scene-ready', this._onSceneReady);
    }

    viewPet()
    {
        this._route.paramMap.subscribe(paramMap => {
            const petIdParam = paramMap.get('petId');
            const petIdFromParam = petIdParam ? +petIdParam : null;

            if (petIdFromParam) {
                this._petService.registerPetForViewing(petIdFromParam).subscribe({
                    next: (pet) =>
                    {
                        //console.log("Registered pet:", pet);
                        this.initialPetData = pet;
                        this._petService.connect(petIdFromParam);
                    },
                    error: () => {
                        alert("Error registering pet. Please try again.");
                    }
                });
            } else
            {
                console.error('Pet ID is missing');
            }
        });
    }
}

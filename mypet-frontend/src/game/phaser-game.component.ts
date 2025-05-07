import { Component, Input, OnInit, OnDestroy } from "@angular/core";
import Phaser from "phaser";
import StartGame from "./main";
import { EventBus } from "./EventBus";
import { MyPet } from "./scenes/MyPet";
import { PetService } from "../app/services/pet.service";
import { Pet } from "../app/models/pet";
import { Subscription } from "rxjs";
import { ActivatedRoute } from "@angular/router";
import { Food } from "../app/models/food";

@Component({
    selector: 'phaser-game',
    template: '<div id="game-container"></div>',
    standalone: true,
})
export class PhaserGame implements OnInit, OnDestroy {

    scene: Phaser.Scene;
    game: Phaser.Game;
    messageSub: Subscription;

    private _onSceneReady = (scene: Phaser.Scene) => {
        this.scene = scene;

        if (scene instanceof MyPet){
            this.messageSub = this._petService.messages$.subscribe(petData => {
                const pet = petData.pet as Pet;
                const foodList = petData.food as Food[];
                const action = petData.action;
                const actionTime = petData.actionTime;
                if (pet) {
                    scene.updatePet(pet, foodList, action, actionTime);
                }
            });
        }

        if (this.sceneCallback){
            this.sceneCallback(scene);
        }
    };

    constructor(
        private _petService: PetService,
        private _route: ActivatedRoute
    ) {}

    sceneCallback: (scene: Phaser.Scene) => void;

    ngOnInit() {
        this.viewPet();

        this.game = StartGame(
            'game-container', 
            this.createFood.bind(this), 
            this.petAPet.bind(this)
        );

        EventBus.on('current-scene-ready', this._onSceneReady);
    }

    ngOnDestroy() {
        if (this.game) {
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

    viewPet() {
        this._route.paramMap.subscribe(paramMap => {
            const petIdParam = paramMap.get('petId');
            const sharedParam = paramMap.get('shared');
            const petIdFromParam = petIdParam ? +petIdParam : null;
            const sharedFromParam = sharedParam ? +sharedParam : 0;

            if (petIdFromParam) {
                this._petService.registerPetForViewing(petIdFromParam).subscribe({
                    next: () => {
                        this._petService.connect(petIdFromParam, sharedFromParam);
                    },
                    error: () => {
                        alert("Error registering pet. Please try again.");
                    }
                });
            } else {
                console.error('Pet ID is missing');
            }
        });
    }

    createFood(petId: number, food: string) {
        this._petService.createPetFood(petId, food);
    }

    petAPet(petId: number) {
        this._petService.petAPet(petId);
    }
}

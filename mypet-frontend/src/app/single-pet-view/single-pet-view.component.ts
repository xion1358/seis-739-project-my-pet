import { Component, ViewChild } from '@angular/core';
import { PhaserGame } from '../../game/phaser-game.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-single-pet-view',
  standalone: true,
  imports: [CommonModule, PhaserGame],
  templateUrl: './single-pet-view.component.html',
  styleUrl: './single-pet-view.component.css'
})
export class SinglePetViewComponent
{
    constructor() {}

    @ViewChild(PhaserGame) phaserRef!: PhaserGame;

}
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { PetService } from '../services/pet.service';
import { PetTypes } from '../models/pettypes';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-pet-selection',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pet-selection.component.html',
  styleUrl: './pet-selection.component.css'
})
export class PetSelectionComponent {
  petTypes: PetTypes[];
  selectedPetType: string;
  petName: string = "Fluffy";

  constructor(private _petService: PetService) {}

  ngOnInit() {
    this._petService.getAllPetTypes();
    this._petService.petTypes$.subscribe({
      next: (petTypes) => this.petTypes = petTypes
    });
  }

  onSelectPet(petType: string): void {
    this.selectedPetType = petType;
  }

  confirmSelection(): void {
    if (this.selectedPetType) {
      this._petService.requestPetForOwner(this.petName, this.selectedPetType)
    }
  }
}

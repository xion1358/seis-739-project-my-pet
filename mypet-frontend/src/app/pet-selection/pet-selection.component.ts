import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-pet-selection',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pet-selection.component.html',
  styleUrl: './pet-selection.component.css'
})
export class PetSelectionComponent {
  petTypes = ['cat', 'dog'];
  selectedPetType: string = 'cat';

  constructor() { }

  onSelectPet(petType: string): void {
    this.selectedPetType = petType;
  }

  confirmSelection(): void {
    // TODO: Send a request to add the pet selected to the user
  }
}

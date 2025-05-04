import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Pet } from '../models/pet';
import { PetService } from '../services/pet.service';

@Component({
  selector: 'catalog',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.css'
})
export class CatalogComponent {
  public sharedPets: Pet[];
  public selectedPet: Pet;
  public page = 1;
  
    constructor(
      private _petService: PetService,
      private _router: Router
    ) {}
  
    ngOnInit() {
      this.getSharedPets();
    }
  
    public viewPet(petId: number, shared: number): void {
      this._router.navigate(["/single-pet-view", petId, shared]);
    }
  
    public selectPet(pet: Pet) {
      this.selectedPet = pet;
    }
  
    private getSharedPets(): void {
      this._petService.queryForSharedPets(this.getCursor(this.sharedPets)).subscribe({
        next: (pets: Pet[]) => {
          this.sharedPets = pets;
          this.sharedPets.sort((pet1, pet2) => pet2.petId - pet1.petId);
        },
        error: (e) => {
          console.error("Error getting pets: ", e);
        }
      })
    }

    public getCursor(pets: Pet[]): number {
      if (pets != null && pets.length > 0) {
        return Math.max(...pets.map(pet => pet.petId));
      } else {
        return 0;
      }
      
    };
}

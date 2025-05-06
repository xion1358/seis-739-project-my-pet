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
  public sharedPets: Pet[] = [];
  public selectedPet: Pet;
  public hasPrevious = false;
  public hasNext = true;
  
    constructor(
      private _petService: PetService,
      private _router: Router
    ) {}
  
    ngOnInit() {
      this.getPage('next');
    }
  
    public viewPet(petId: number, shared: number): void {
      this._router.navigate(["/single-pet-view", petId, shared]);
    }
  
    public selectPet(pet: Pet) {
      this.selectedPet = pet;
    }

    public getNextPage(){
      this.getPage('next');
    }

    public getPreviousPage() {
      this.getPage('previous');
    }

    private getPage(pageDirection: string) {
      this._petService.queryForSharedPets(this.getCursor(pageDirection), pageDirection).subscribe({
        next: (response: any) => {
          if (response.pets.length >= 1) {
            this.sharedPets = response.pets;
            this.hasNext = response.hasNext;
            this.hasPrevious = response.hasPrevious;
            this.sharedPets.sort((pet1, pet2) => pet1.petId - pet2.petId);
          } else {
            if (pageDirection === "next") {
              this.hasNext = false;
            } else {
              this.hasPrevious = false;
            }
            alert("You've reached the end or beginning of the catalog! You may want to refresh your page.");
          }
        },
        error: (e) => {
          console.error("Error getting pets: ", e);
        }
      });
    }

    private getCursor(pageDirection: string): number {
      if (this.sharedPets === null || this.sharedPets.length < 1) {
        return 0;
      } else {
        if (pageDirection === "next") {
          return Math.max(...this.sharedPets.map(pet => pet.petId));
        }else {
          return Math.min(...this.sharedPets.map(pet => pet.petId));
        }
      }
    };
}

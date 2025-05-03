import { Component } from '@angular/core';
import { PetService } from '../services/pet.service';
import { Pet } from '../models/pet';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-my-page',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './my-page.component.html',
  styleUrls: ['./my-page.component.css']
})
export class MyPageComponent {
  public myPets: Pet[];
  public selectedPet: Pet;

  constructor(
    private _petService: PetService,
    private _router: Router
  ) {}

  ngOnInit() {
    this.syncPets();
  }

  public viewPet(petId: number): void {
    this._router.navigate(["/single-pet-view", petId]);
  }

  public selectPet(pet: Pet) {
    this.selectedPet = pet;
  }

  public abandonPet(pet: Pet) {
    this._petService.abandonThisPet(pet.petId).subscribe({
      next: (response) => {
        if (response) {
          this.syncPets();
        } else {
          alert("Sorry, couldn't abandon the pet at this time. Please try again later");
        }
      },
      error: (error) => {
        // console.error("Failed to abandon pet ", error);
        alert("Sorry, couldn't abandon the pet at this time. Please try again later");
      }
    })
  }

  private syncPets(): void {
    this._petService.queryForPets().subscribe({
      next: (pets: Pet[]) => {
        this.myPets = pets;
        this.myPets.sort((pet1, pet2) => pet2.petId - pet1.petId);
      },
      error: (e) => {
        console.error("Error getting pets: ", e);
      }
    })
  }
}

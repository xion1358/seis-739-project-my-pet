import { ChangeDetectorRef, Component } from '@angular/core';
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
  public selectedPet: Pet | null = null;

  constructor(
    private _petService: PetService,
    private _router: Router
  ) {}

  ngOnInit() {
    this.syncPets();
  }

  public viewPet(petId: number, shared: number): void {
    this._router.navigate(["/single-pet-view", petId, shared]);
  }

  public selectPet(pet: Pet) {
    this.selectedPet = pet;
  }

  public sharePet(pet: Pet) {
    this._petService.shareThisPet(pet.petId).subscribe({
      next: () => {
        this.syncPets();
        console.log("Shared your pet {}", pet.petId);
      },
      error: (error) => {
        // console.error("Failed to abandon pet ", error);
        alert("Sorry, couldn't share the pet at this time. Please try again later");
      }
    })
  }

  public unsharePet(pet: Pet) {
    this._petService.unshareThisPet(pet.petId).subscribe({
      next: () => {
        this.syncPets();
        console.log("Unshared your pet {}", pet.petId);
      },
      error: (error) => {
        // console.error("Failed to abandon pet ", error);
        alert("Sorry, couldn't unshare the pet at this time. Please try again later");
      }
    })
  }

  public abandonPet(pet: Pet) {
    this._petService.abandonThisPet(pet.petId).subscribe({
      next: (response) => {
        if (response) {
          this.syncPets();
          this.selectedPet = null;
        } else {
          alert("Sorry, couldn't abandon the pet at this time. Please check if your pet is still shared and try again.");
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
        this.myPets.sort((pet1, pet2) => pet1.petId- pet2.petId);
        const current = this.selectedPet;
        if (current != null) {
          this.selectedPet = this.myPets.find(pet => pet.petId === current.petId) || null;
        }
        
      },
      error: (e) => {
        console.error("Error getting pets: ", e);
      }
    })
  }
}

import { Component } from '@angular/core';
import { MyPetHeaderComponent } from "../my-pet-header/my-pet-header.component";

@Component({
  selector: 'app-landing-page',
  standalone: true,
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.css'
})
export class LandingPageComponent {
  public register(){
    alert("Registering...");
  }
}

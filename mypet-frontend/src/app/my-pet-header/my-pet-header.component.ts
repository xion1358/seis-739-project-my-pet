import { Component } from '@angular/core';

@Component({
  selector: 'app-my-pet-header',
  standalone: true,
  imports: [],
  templateUrl: './my-pet-header.component.html',
  styleUrl: './my-pet-header.component.css'
})
export class MyPetHeaderComponent {
  public login() {
    alert("Logging in...");
  }
}

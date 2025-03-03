import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-my-pet-header',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './my-pet-header.component.html',
  styleUrl: './my-pet-header.component.css'
})
export class MyPetHeaderComponent {
  public login() {
    alert("Logging in...");
  }
}

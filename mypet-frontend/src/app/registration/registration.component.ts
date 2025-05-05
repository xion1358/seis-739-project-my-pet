import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-registration',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.css'
})
export class RegistrationComponent {
  public username: string;
  public displayName: string;
  public email: string;
  public password: string;


  constructor(private _authServ: AuthenticationService) {}

  public register(form: NgForm): void {
      if (!form.valid) {
        Object.values(form.controls).forEach(control => {
          control.markAsTouched();
        });
        return;
      } else {
        this._authServ.registration([this.username, this.displayName, this.email, this.password]);
      }
    }
}

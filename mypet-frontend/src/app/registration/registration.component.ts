import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';

@Component({
  selector: 'app-registration',
  standalone: true,
  imports: [RouterModule, FormsModule],
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.css'
})
export class RegistrationComponent {
  public email: string;
  public username: string;
  public displayName: string;
  public password: string;


  constructor(private _authServ: AuthenticationService) {}

  public register(form: NgForm): void {
      if (!form.valid) {
        return;
      } else {
        this._authServ.registration([this.username, this.displayName, this.email, this.password]);
      }
    }
}

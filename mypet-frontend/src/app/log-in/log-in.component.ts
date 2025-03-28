import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule, NgForm } from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';

@Component({
  selector: 'app-log-in',
  standalone: true,
  imports: [RouterModule, FormsModule],
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.css']
})
export class LogInComponent {
  public username: string = '';
  public password: string = '';

  private loginStatus = false;

  constructor(private _authServ: AuthenticationService) {}

  public login(form: NgForm): void {
    this.loginStatus = false;
    if (!form.valid) {
      this.loginStatus = false;
      return;
    } else {
      this._authServ.login([this.username, this.password]);
    }
  }
}

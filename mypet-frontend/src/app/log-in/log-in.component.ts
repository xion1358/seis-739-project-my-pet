import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule, NgForm } from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-log-in',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.css']
})
export class LogInComponent {
  public username: string = '';
  public password: string = '';

  constructor(private _authServ: AuthenticationService) {}

  public login(form: NgForm): void {
    if (!form.valid) {
      Object.values(form.controls).forEach(control => {
        control.markAsTouched();
      });
      return;
    } else {
      this._authServ.login([this.username, this.password]);
    }
  }
}

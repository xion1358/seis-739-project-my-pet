import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-my-pet-header',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './my-pet-header.component.html',
  styleUrl: './my-pet-header.component.css'
})
export class MyPetHeaderComponent {
  public loggedIn: boolean = false;
  private _statusSub: Subscription;

  constructor(private _authService: AuthenticationService, private _router: Router){}

  ngOnInit() {
    this._statusSub = this._authService.loggedInStatus$.subscribe(status => {
      this.loggedIn = status;
    })
  }

  public logoff() {
    this._authService.logoff();
    this._router.navigate(['/']);
  }

  ngOnDestroy() {
    if (this._statusSub) {
      this._statusSub.unsubscribe();
    }
  }
}

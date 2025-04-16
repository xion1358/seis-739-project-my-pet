import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

import { MyPetHeaderComponent } from "./my-pet-header/my-pet-header.component";
import { AuthenticationService } from './services/authentication.service';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [CommonModule, RouterOutlet, MyPetHeaderComponent],
    templateUrl: './app.component.html'
})
export class AppComponent 
{
    constructor(private _authService: AuthenticationService) {}

    ngOnInit() {
        this._authService.validateLogin();
    }

}

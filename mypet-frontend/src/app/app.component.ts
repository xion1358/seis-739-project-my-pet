import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
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
    constructor(
        private _authService: AuthenticationService,
        private _router: Router
    ) {}

    ngOnInit() {
        // Record the last known navigated route 
        this._router.events.subscribe((event) => {
            if (event instanceof NavigationEnd) {
                sessionStorage.setItem('lastNavigatedRoute', event.urlAfterRedirects);
            }
        })

        // On any refresh, revalidate
        this._authService.validateLogin();
    }

}

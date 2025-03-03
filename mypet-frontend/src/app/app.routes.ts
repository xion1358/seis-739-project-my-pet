import { Routes } from '@angular/router';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { LogInComponent } from './log-in/log-in.component';

export const appRoutes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'log-in', component: LogInComponent},
];

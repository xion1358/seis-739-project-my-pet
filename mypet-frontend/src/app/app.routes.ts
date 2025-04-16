import { Routes } from '@angular/router';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { LogInComponent } from './log-in/log-in.component';
import { RegistrationComponent } from './registration/registration.component';
import { MyPageComponent } from './my-page/my-page.component';
import { SinglePetViewComponent } from './single-pet-view/single-pet-view.component';

export const appRoutes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'log-in', component: LogInComponent},
  { path: 'registration', component: RegistrationComponent},
  { path: 'mypage', component: MyPageComponent},
  { path: 'single-pet-view', component: SinglePetViewComponent},
];

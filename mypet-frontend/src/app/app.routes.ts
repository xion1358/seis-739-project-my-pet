import { Routes } from '@angular/router';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { LogInComponent } from './log-in/log-in.component';
import { RegistrationComponent } from './registration/registration.component';
import { MyPageComponent } from './my-page/my-page.component';
import { SinglePetViewComponent } from './single-pet-view/single-pet-view.component';
import { PetSelectionComponent } from './pet-selection/pet-selection.component';
import { CatalogComponent } from './catalog/catalog.component';

export const appRoutes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'log-in', component: LogInComponent},
  { path: 'registration', component: RegistrationComponent},
  { path: 'pet-selection', component: PetSelectionComponent},
  { path: 'mypage', component: MyPageComponent},
  { path: 'single-pet-view/:petId/:shared', component: SinglePetViewComponent},
  { path: 'catalog', component: CatalogComponent},
];

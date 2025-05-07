import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MyPetHeaderComponent } from './my-pet-header.component';
import { AuthenticationService } from '../services/authentication.service';
import { BehaviorSubject } from 'rxjs';
import { Router, RouterModule, Routes } from '@angular/router';
import { MenuBarComponent } from '../menu-bar/menu-bar.component';
import { By } from '@angular/platform-browser';
import { LandingPageComponent } from '../landing-page/landing-page.component';
import { CatalogComponent } from '../catalog/catalog.component';
import { LogInComponent } from '../log-in/log-in.component';

describe('MyPetHeaderComponent', () => {
  let component: MyPetHeaderComponent;
  let fixture: ComponentFixture<MyPetHeaderComponent>;
  let mockAuthService: jasmine.SpyObj<AuthenticationService>;
  let router: Router;
  let status$: BehaviorSubject<boolean>;

  const routes: Routes = [
      { path: '', component: LandingPageComponent },
      { path: 'catalog', component: CatalogComponent },
      { path: 'log-in', component: LogInComponent},
    ];

  beforeEach(async () => {
    status$ = new BehaviorSubject<boolean>(false);
    mockAuthService = jasmine.createSpyObj('AuthenticationService', ['logoff'], { loggedInStatus$: status$ });

    await TestBed.configureTestingModule({
      imports: [MyPetHeaderComponent, MenuBarComponent, RouterModule.forRoot(routes)],
      providers: [{ provide: AuthenticationService, useValue: mockAuthService }]
    }).compileComponents();

    fixture = TestBed.createComponent(MyPetHeaderComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show "Log in" button when not logged in', () => {
    status$.next(false);
    fixture.detectChanges();

    const button = fixture.debugElement.query(By.css('.header-button'));
    expect(button.nativeElement.textContent).toContain('Log in');
  });

  it('should show "Log off" button when logged in', () => {
    status$.next(true);
    fixture.detectChanges();

    const button = fixture.debugElement.query(By.css('.header-button'));
    expect(button.nativeElement.textContent).toContain('Log off');
  });

  it('should call logoff() and navigate to "/" on "Log off" click', () => {
    spyOn(router, 'navigate');
    status$.next(true);
    fixture.detectChanges();

    const button = fixture.debugElement.query(By.css('.header-button'));
    button.triggerEventHandler('click', new MouseEvent('click'));
    fixture.detectChanges();

    expect(mockAuthService.logoff).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should link the title to "/" when not logged in', () => {
    status$.next(false);
    fixture.detectChanges();

    const anchor = fixture.debugElement.query(By.css('.Title a'));
    expect(anchor.attributes['ng-reflect-router-link']).toBe('/');
  });

  it('should link the title to "/mypage" when logged in', () => {
    status$.next(true);
    fixture.detectChanges();

    const anchor = fixture.debugElement.query(By.css('.Title a'));
    expect(anchor.attributes['ng-reflect-router-link']).toBe('/mypage');
  });

  it('should show menu-bar only when logged in', () => {
    status$.next(true);
    fixture.detectChanges();

    const menuBar = fixture.debugElement.query(By.css('menu-bar'));
    expect(menuBar).toBeTruthy();

    status$.next(false);
    fixture.detectChanges();

    const menuBarAfter = fixture.debugElement.query(By.css('menu-bar'));
    expect(menuBarAfter).toBeNull();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegistrationComponent } from './registration.component';
import { FormsModule, NgForm } from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';
import { By } from '@angular/platform-browser';
import { Router, RouterModule, Routes } from '@angular/router';
import { LandingPageComponent } from '../landing-page/landing-page.component';

describe('RegistrationComponent', () => {
  let component: RegistrationComponent;
  let fixture: ComponentFixture<RegistrationComponent>;
  let authService: jasmine.SpyObj<AuthenticationService>;
  let router: Router;

  const routes: Routes = [
        { path: '', component: LandingPageComponent },
      ];

  beforeEach(async () => {
    authService = jasmine.createSpyObj('AuthenticationService', ['registration']);

    await TestBed.configureTestingModule({
      imports: [RegistrationComponent, FormsModule, RouterModule.forRoot(routes)],
      providers: [
        { provide: AuthenticationService, useValue: authService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegistrationComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show error messages when form fields are invalid', () => {
    const usernameInput = fixture.debugElement.query(By.css('input[name="username"]')).nativeElement;
    usernameInput.value = '';
    usernameInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const errorMessage = fixture.debugElement.query(By.css('.error-message')).nativeElement;
    expect(errorMessage).toBeTruthy();
  });

  it('should call the registration method when the form is valid', () => {
    component.username = 'validUser';
    component.displayName = 'Valid User';
    component.email = 'valid@example.com';
    component.password = 'validPassword';

    const form: NgForm = fixture.debugElement.query(By.css('form')).injector.get(NgForm);
    
    spyOnProperty(form, 'valid', 'get').and.returnValue(true);

    component.register(form);
    expect(authService.registration).toHaveBeenCalledWith([
      'validUser',
      'Valid User',
      'valid@example.com',
      'validPassword'
    ]);
  });

  it('should not call registration when the form is invalid', () => {
    const form: NgForm = fixture.debugElement.query(By.css('form')).injector.get(NgForm);
    
    spyOnProperty(form, 'valid', 'get').and.returnValue(false);

    component.register(form);
    expect(authService.registration).not.toHaveBeenCalled();
  });

  it('should enable the submit button when the form is valid', () => {
    const submitButton = fixture.debugElement.query(By.css('.submit-button')).nativeElement;
    const form: NgForm = fixture.debugElement.query(By.css('form')).injector.get(NgForm);

    spyOnProperty(form, 'valid', 'get').and.returnValue(true);
    fixture.detectChanges();

    expect(submitButton.disabled).toBeFalse();
  });
});

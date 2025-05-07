import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, NgForm } from '@angular/forms';
import { LogInComponent } from './log-in.component';
import { AuthenticationService } from '../services/authentication.service';
import { CommonModule } from '@angular/common';
import { By } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';
import { LandingPageComponent } from '../landing-page/landing-page.component';

describe('LogInComponent', () => {
  let component: LogInComponent;
  let fixture: ComponentFixture<LogInComponent>;
  let authService: AuthenticationService;
  let compiled: HTMLElement;
  let usernameInput: HTMLInputElement;
  let passwordInput: HTMLInputElement;
  let submitButton: HTMLButtonElement;
  const routes: Routes = [
      { path: '', component: LandingPageComponent },
    ]

  beforeEach(async () => {
    const authServiceMock = {
      login: jasmine.createSpy('login'),
    };

    await TestBed.configureTestingModule({
      imports: [CommonModule, RouterModule.forRoot(routes), FormsModule],
      providers: [{ provide: AuthenticationService, useValue: authServiceMock }],
    }).compileComponents();

    fixture = TestBed.createComponent(LogInComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthenticationService);
    compiled = fixture.nativeElement;

    usernameInput = compiled.querySelector('#username')!;
    passwordInput = compiled.querySelector('#password')!;
    submitButton = compiled.querySelector('.submit-button')!;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the form with input fields and a submit button', () => {
    expect(compiled.querySelector('form')).toBeTruthy();
    expect(usernameInput).toBeTruthy();
    expect(passwordInput).toBeTruthy();
    expect(submitButton).toBeTruthy();
    expect(submitButton.textContent).toContain('Submit');
  });

  it('should bind username and password inputs to component properties', () => {
    usernameInput.value = 'testuser';
    passwordInput.value = 'testpass';
    usernameInput.dispatchEvent(new Event('input'));
    passwordInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(component.username).toBe('testuser');
    expect(component.password).toBe('testpass');
  });

  /**
   * Note: due to the way we designed the view, we will never see the message for minlength. 
   * However the server does test this. So I am going to leave the minlength message in, but we won't be testing for it below.
   */
  it('should display error messages for invalid username input', () => {
    const usernameErrors = [
      { value: '', error: 'Username is required.' },
      { value: 'a'.repeat(256), error: 'Username cannot be more than 255 characters long.' },
      { value: 'test!', error: 'Username must contain only alphanumeric characters and underscores.' },
    ];
  
    usernameErrors.forEach(({ value, error }) => {
      usernameInput.value = value;
      usernameInput.dispatchEvent(new Event('input'));
      usernameInput.dispatchEvent(new Event('blur'));
      fixture.detectChanges();
  
      const errorElements = compiled.querySelectorAll('.error-message');
  
      const errorElement = compiled.querySelector('.error-message');
      expect(errorElement).toBeTruthy();
      expect(errorElement?.textContent).toContain(error);
    });
  });

  /**
   * Note: due to the way we designed the view, we will never see the message for minlength. 
   * However the server does test this. So I am going to leave the minlength message in, but we won't be testing for it below.
   */
  it('should display error messages for invalid password input', () => {
    const passwordErrors = [
      { value: '', error: 'Password is required.' },
      { value: 'p'.repeat(256), error: 'Password cannot be more than 255 characters long.' },
    ];
  
    passwordErrors.forEach(({ value, error }) => {
      passwordInput.value = value;
      passwordInput.dispatchEvent(new Event('input'));
      passwordInput.dispatchEvent(new Event('blur'));
      fixture.detectChanges();
  
      const errorElements = fixture.nativeElement.querySelectorAll('.error-message');
  
      const errorElement = fixture.nativeElement.querySelector('.error-message');
      expect(errorElement).toBeTruthy();
      expect(errorElement.textContent).toContain(error);
    });
  });
  

  it('should call authService.login with correct credentials when form is submitted and valid', () => {
    usernameInput.value = 'validuser';
    passwordInput.value = 'validpass';
    usernameInput.dispatchEvent(new Event('input'));
    passwordInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const form = fixture.debugElement.query(By.directive(NgForm)).nativeElement;
    form.dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(authService.login).toHaveBeenCalledWith(['validuser', 'validpass']);
  });

  it('should not call authService.login when form is submitted and invalid', () => {
    usernameInput.value = '';
    passwordInput.value = '';
    usernameInput.dispatchEvent(new Event('input'));
    passwordInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const form = fixture.debugElement.query(By.directive(NgForm)).nativeElement;
    form.dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(authService.login).not.toHaveBeenCalled();
  });

  it('should mark form controls as touched when form is submitted and invalid', () => {
    usernameInput.value = '';
    passwordInput.value = '';
    fixture.detectChanges();

    const form = fixture.debugElement.query(By.directive(NgForm)).nativeElement;
    form.dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(usernameInput.classList.contains('ng-touched')).toBeTruthy();
    expect(passwordInput.classList.contains('ng-touched')).toBeTruthy();
  });
});


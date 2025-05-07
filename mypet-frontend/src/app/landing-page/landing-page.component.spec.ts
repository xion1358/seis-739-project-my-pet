import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LandingPageComponent } from './landing-page.component';
import { RouterModule, Routes } from '@angular/router';
import { RegistrationComponent } from '../registration/registration.component';

describe('LandingPageComponent', () => {
  let component: LandingPageComponent;
  let fixture: ComponentFixture<LandingPageComponent>;
  const routes: Routes = [
    { path: 'registration', component: RegistrationComponent},
  ]

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        LandingPageComponent,
        RouterModule.forRoot(routes),
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LandingPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the registration button', () => {
    const button = fixture.debugElement.nativeElement.querySelector('button[name="registration-button"]');
    expect(button).toBeTruthy();
    expect(button.textContent).toContain('Register');
  });

  it('should have the creature image', () => {
    const img = fixture.debugElement.nativeElement.querySelector('img[alt="MyPetCreature"]');
    expect(img).toBeTruthy();
    expect(img.src).toContain('assets/MyPetCreature.png');
  });

  it('should show the main slogan text', () => {
    const text = fixture.debugElement.nativeElement.textContent;
    expect(text).toContain('Make a new friend today!');
    expect(text).toContain('Welcome To A New Virtual Pet Experience!');
  });

  it('should have 2 video placeholders', () => {
    const placeholders = fixture.debugElement.nativeElement.querySelectorAll('.video-placeholder');
    expect(placeholders.length).toBe(2);
  });

  it('should list all game description items', () => {
    const titles = fixture.debugElement.nativeElement.querySelectorAll('.description-title');
    const descriptions = fixture.debugElement.nativeElement.querySelectorAll('.description-text');
    expect(titles.length).toBe(6);
    expect(descriptions.length).toBe(6);

    expect(titles[0].textContent).toContain('Pick from a variety of pets');
    expect(descriptions[0].textContent).toContain('Dogs, cats, and more');
  });
});

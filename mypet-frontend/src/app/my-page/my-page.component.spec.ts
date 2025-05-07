import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MyPageComponent } from './my-page.component';
import { PetService } from '../services/pet.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('MyPageComponent', () => {
  let component: MyPageComponent;
  let fixture: ComponentFixture<MyPageComponent>;
  let mockPetService: any;
  let mockRouter: any;
  let mockActivatedRoute: any;

  const mockPets = [
    {
      petId: 1,
      petName: 'Fido',
      petOwner: 'Jack',
      petType: { name: 'dog' },
      petAffectionLevel: 50,
      petHungerLevel: 30,
      petYLocation: 100,
      petXLocation: 300,
      petDirection: 'right',
      petAction: 'idle',
      shared: 0
    },
    {
      petId: 2,
      petName: 'Whiskers',
      petOwner: 'Jack',
      petType: { name: 'cat' },
      petAffectionLevel: 50,
      petHungerLevel: 30,
      petYLocation: 100,
      petXLocation: 300,
      petDirection: 'right',
      petAction: 'idle',
      shared: 0
    }
  ];

  beforeEach(async () => {
    mockPetService = {
      queryForPets: jasmine.createSpy().and.returnValue(of(mockPets)),
      shareThisPet: jasmine.createSpy().and.returnValue(of({})),
      unshareThisPet: jasmine.createSpy().and.returnValue(of({})),
      abandonThisPet: jasmine.createSpy().and.returnValue(of(true))
    };

    mockRouter = {
      navigate: jasmine.createSpy()
    };

    mockActivatedRoute = {
      snapshot: { params: { petId: 1 } }
    };

    await TestBed.configureTestingModule({
      imports: [MyPageComponent],
      providers: [
        { provide: PetService, useValue: mockPetService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MyPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load pets on init', () => {
    expect(component.myPets.length).toBe(2);
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Fido');
    expect(compiled.textContent).toContain('Whiskers');
  });

  it('should show no pets message if list is empty', () => {
    mockPetService.queryForPets.and.returnValue(of([]));
    component.ngOnInit();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('No pets found for the current user.');
  });

  it('should select a pet when clicked', () => {
    const petCards = fixture.debugElement.queryAll(By.css('.pet-card'));
    console.log(petCards);
    petCards[0].nativeElement.click();
    expect(component.selectedPet?.petId).toBe(1);
  });

  it('should navigate to single pet view', () => {
    component.viewPet(1, 0);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/single-pet-view', 1, 0]);
  });

  it('should share pet and sync pets', () => {
    component.sharePet(mockPets[0]);
    expect(mockPetService.shareThisPet).toHaveBeenCalledWith(1);
    expect(mockPetService.queryForPets).toHaveBeenCalledTimes(2);
  });

  it('should unshare pet and sync pets', () => {
    component.unsharePet(mockPets[1]);
    expect(mockPetService.unshareThisPet).toHaveBeenCalledWith(2);
    expect(mockPetService.queryForPets).toHaveBeenCalledTimes(2);
  });

  it('should abandon pet and deselect if successful', () => {
    component.selectedPet = mockPets[0];
    component.abandonPet(mockPets[0]);
    expect(mockPetService.abandonThisPet).toHaveBeenCalledWith(1);
    expect(component.selectedPet).toBeNull();
  });

  it('should show alert on abandon failure response', () => {
    spyOn(window, 'alert');
    mockPetService.abandonThisPet.and.returnValue(of(false));
    component.abandonPet(mockPets[0]);
    expect(window.alert).toHaveBeenCalledWith(jasmine.stringMatching(/couldn't abandon/));
  });

  it('should alert on error in sharePet', () => {
    spyOn(window, 'alert');
    mockPetService.shareThisPet.and.returnValue(throwError(() => new Error('fail')));
    component.sharePet(mockPets[0]);
    expect(window.alert).toHaveBeenCalled();
  });

  it('should alert on error in unsharePet', () => {
    spyOn(window, 'alert');
    mockPetService.unshareThisPet.and.returnValue(throwError(() => new Error('fail')));
    component.unsharePet(mockPets[0]);
    expect(window.alert).toHaveBeenCalled();
  });

  it('should alert on error in abandonPet', () => {
    spyOn(window, 'alert');
    mockPetService.abandonThisPet.and.returnValue(throwError(() => new Error('fail')));
    component.abandonPet(mockPets[0]);
    expect(window.alert).toHaveBeenCalled();
  });

  it('should alert on error in syncPets', () => {
    spyOn(console, 'error');
    mockPetService.queryForPets.and.returnValue(throwError(() => new Error('fail')));
    component['syncPets'](); // direct call to private method
    expect(console.error).toHaveBeenCalledWith('Error getting pets: ', jasmine.any(Error));
  });
});

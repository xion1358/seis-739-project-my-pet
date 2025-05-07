import { TestBed, ComponentFixture, async, waitForAsync } from '@angular/core/testing';
import { Router } from '@angular/router';
import { CatalogComponent } from './catalog.component';
import { PetService } from '../services/pet.service';
import { of } from 'rxjs';
import { Pet } from '../models/pet';
import { SharedPetsResponse } from '../models/shared-pets-response';

describe('CatalogComponent', () => {
  let component: CatalogComponent;
  let fixture: ComponentFixture<CatalogComponent>;
  let petService: jasmine.SpyObj<PetService>;
  let router: Router;

  beforeEach(waitForAsync(() => {
    const petServiceSpy = jasmine.createSpyObj<PetService>('PetService', ['queryForSharedPets']);
  
    TestBed.configureTestingModule({
      imports: [CatalogComponent],
      providers: [{ provide: PetService, useValue: petServiceSpy }]
    }).compileComponents();
  
    fixture = TestBed.createComponent(CatalogComponent);
    component = fixture.componentInstance;
    petService = TestBed.inject(PetService) as jasmine.SpyObj<PetService>;
    router = TestBed.inject(Router);
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load shared pets on initialization', () => {
    const mockResponse: SharedPetsResponse = {
      pets: [{
        petId: 1, 
        petName: 'Test Pet', 
        petAffectionLevel: 5, 
        petHungerLevel: 3, 
        petOwner: 'Owner',
        petType: { name: 'dog' },
        petXLocation: 0,
        petYLocation: 0,
        petDirection: 'right',
        petAction: 'idle',
        shared: 0
      }],
      hasNext: true,
      hasPrevious: false
    };
    petService.queryForSharedPets.and.returnValue(of(mockResponse));

    fixture.detectChanges();

    expect(component.sharedPets.length).toBe(1);
    expect(component.hasNext).toBe(true);
    expect(component.hasPrevious).toBe(false);
  });

  it('should select a pet', () => {
    const pet: Pet = {
      petId: 1, 
      petName: 'Test Pet', 
      petAffectionLevel: 5, 
      petHungerLevel: 3, 
      petOwner: 'Owner',
      petType: { name: 'dog' },
      petXLocation: 0,
      petYLocation: 0,
      petDirection: 'right',
      petAction: 'idle',
      shared: 0
    };

    component.selectPet(pet);

    expect(component.selectedPet).toEqual(pet);
  });

  it('should navigate to view a pet', () => {
    const petId = 1;
    const shared = 1;
    const navigateSpy = spyOn(router, 'navigate');

    component.viewPet(petId, shared);

    expect(navigateSpy).toHaveBeenCalledWith(['/single-pet-view', petId, shared]);
  });

  it('should load next page of shared pets', () => {
    const mockResponse: SharedPetsResponse = {
      pets: [{
        petId: 2, 
        petName: 'Test Pet 2', 
        petAffectionLevel: 5, 
        petHungerLevel: 3, 
        petOwner: 'Owner',
        petType: { name: 'dog' },
        petXLocation: 0,
        petYLocation: 0,
        petDirection: 'right',
        petAction: 'idle',
        shared: 0
      }],
      hasNext: true,
      hasPrevious: true
    };
    petService.queryForSharedPets.and.returnValue(of(mockResponse));

    component.sharedPets = [{
      petId: 3, 
      petName: 'Test Pet 3', 
      petAffectionLevel: 5, 
      petHungerLevel: 3, 
      petOwner: 'Owner',
      petType: { name: 'dog' },
      petXLocation: 0,
      petYLocation: 0,
      petDirection: 'right',
      petAction: 'idle',
      shared: 0
    }];

    component.getNextPage();

    expect(component.sharedPets.length).toBe(1);
    expect(component.hasNext).toBe(true);
    expect(component.hasPrevious).toBe(true);
  });

  it('should load previous page of shared pets', () => {
    const mockResponse: SharedPetsResponse = {
      pets: [{
        petId: 1, 
        petName: 'Test Pet', 
        petAffectionLevel: 5, 
        petHungerLevel: 3, 
        petOwner: 'Owner',
        petType: { name: 'dog' },
        petXLocation: 0,
        petYLocation: 0,
        petDirection: 'right',
        petAction: 'idle',
        shared: 0
      }],
      hasNext: true,
      hasPrevious: false
    };

    petService.queryForSharedPets.and.returnValue(of(mockResponse));
    component.sharedPets = [{
      petId: 2, 
      petName: 'Next Pet', 
      petAffectionLevel: 4, 
      petHungerLevel: 2, 
      petOwner: 'Owner2',
      petType: { name: 'dog' },
      petXLocation: 0,
      petYLocation: 0,
      petDirection: 'right',
      petAction: 'idle',
      shared: 0
    }];

    component.getPreviousPage();

    expect(component.sharedPets.length).toBe(1);
    expect(component.hasNext).toBe(true);
    expect(component.hasPrevious).toBe(false);
  });
});

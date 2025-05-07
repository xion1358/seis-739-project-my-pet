import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PetService } from './pet.service';
import { Pet } from '../models/pet';
import { Utility } from '../Utilities/Utility';
import { Router } from '@angular/router';
import { HttpHeaders } from '@angular/common/http';  // Import HttpHeaders
import { PetTypes } from '../models/pettypes';
import { SharedPetsResponse } from '../models/shared-pets-response';

describe('PetService', () => {
  let service: PetService;
  let httpMock: HttpTestingController;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        PetService,
        { provide: Router, useValue: routerSpy }
      ]
    });
    service = TestBed.inject(PetService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should retrieve all pets', () => {
    const mockPets: Pet[] = [{
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
    }];

    const mockHeaders = new HttpHeaders().set('Authorization', 'Bearer mock-token');
    
    spyOn(Utility, 'getTokenHeader').and.returnValue(mockHeaders);
    spyOn(Utility, 'getUserName').and.returnValue('Owner');

    service.queryForPets().subscribe(pets => {
      expect(pets.length).toBe(1);
      expect(pets[0].petName).toBe('Test Pet');
    });

    const req = httpMock.expectOne(request => request.url === `${service['_serverURL']}/get-pets`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    req.flush(mockPets);
  });

  it('should register pet for viewing', () => {
    const petId = 1;
    const mockPet: Pet = {
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

    const mockHeaders = new HttpHeaders().set('Authorization', 'Bearer mock-token');

    spyOn(Utility, 'getTokenHeader').and.returnValue(mockHeaders);
    spyOn(Utility, 'getUserName').and.returnValue('Owner');

    service.registerPetForViewing(petId).subscribe((pet) => {
      expect(pet.petName).toBe('Test Pet');
    });

    const req = httpMock.expectOne(request => request.url === `${service['_serverURL']}/register-pet-for-viewing`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    req.flush(mockPet);
  });

  it('should query for shared pets', () => {
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

    const cursor = 0;
    const pageDirection = 'next';
    const mockHeaders = new HttpHeaders().set('Authorization', 'Bearer mock-token');

    spyOn(Utility, 'getTokenHeader').and.returnValue(mockHeaders);
    spyOn(Utility, 'getUserName').and.returnValue('Owner');

    service.queryForSharedPets(cursor, pageDirection).subscribe(response => {
      expect(response.pets.length).toBe(1);
      expect(response.hasNext).toBe(true);
      expect(response.hasPrevious).toBe(false);
    });

    const req = httpMock.expectOne(request => request.url === `${service['_serverURL']}/get-shared-pets`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    req.flush(mockResponse);
  });

  it('should call createPetFood', () => {
    const petId = 1;
    const foodType = 'bone';

    const mockHeaders = new HttpHeaders().set('Authorization', 'Bearer mock-token');

    spyOn(Utility, 'getTokenHeader').and.returnValue(mockHeaders);
    spyOn(Utility, 'getUserName').and.returnValue('Owner');

    service.createPetFood(petId, foodType);

    const req = httpMock.expectOne(request => request.url === `${service['_serverURL']}/create-pet-food`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    expect(req.request.params.get('id')).toBe(petId.toString());
    expect(req.request.params.get('food')).toBe(foodType);
    req.flush(true);
  });

  it('should call petAPet', () => {
    const petId = 1;

    const mockHeaders = new HttpHeaders().set('Authorization', 'Bearer mock-token');

    spyOn(Utility, 'getTokenHeader').and.returnValue(mockHeaders);
    spyOn(Utility, 'getUserName').and.returnValue('Owner');

    service.petAPet(petId);

    const req = httpMock.expectOne(request => request.url === `${service['_serverURL']}/pet-a-pet`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    expect(req.request.params.get('id')).toBe(petId.toString());
    req.flush(true);
  });

  afterEach(() => {
    httpMock.verify();
  });
  
  it('should attempt to create a Client when connect is called and call the subscribeToPet on connect', () => {
    const subscribeSpy = spyOn(service as any, 'subscribeToPet');
    const petId = 1;
    const shared = 0;
    const mockToken = 'mock-token';

    spyOn(localStorage, 'getItem').and.returnValue(mockToken);

    service.connect(petId, shared);

    const stompClient = service.getStompClient();

    stompClient.onConnect!({} as any);

    expect(subscribeSpy).toHaveBeenCalledWith(petId, shared);
    expect(stompClient).toBeTruthy();

  });
  
  

  it('should subscribe to the pet topic and handle messages', () => {
    const petId = 1;
    const shared = 0;
    const mockMessage = { body: JSON.stringify({ petId, petName: 'Test Pet' }) };
    const mockStompClient = jasmine.createSpyObj('Client', ['subscribe', 'deactivate']);
    const serviceInstance = TestBed.inject(PetService);
    (serviceInstance as any)._stompClient = mockStompClient;

    spyOn(serviceInstance['_messageSubject'], 'next');

    serviceInstance.subscribeToPet(petId, shared);
    mockStompClient.subscribe.calls.argsFor(0)[1](mockMessage);

    expect(serviceInstance['_messageSubject'].next).toHaveBeenCalledWith({ petId, petName: 'Test Pet' });
  });

  it('should unsubscribe from the pet topic', () => {
    const mockStompClient = jasmine.createSpyObj('Client', ['deactivate']);
    const serviceInstance = TestBed.inject(PetService);
    (serviceInstance as any)._stompClient = mockStompClient;

    serviceInstance.unsubscribeFromViewingPet();

    expect(mockStompClient.deactivate).toHaveBeenCalled();
  });

  it('should retrieve pet types and update the pet types subject', () => {
    const mockPetTypes: PetTypes[] = [{ name: 'dog' }, { name: 'cat' }];
    const mockHeaders = new HttpHeaders().set('Authorization', 'Bearer mock-token');
    spyOn(Utility, 'getTokenHeader').and.returnValue(mockHeaders);

    const serviceInstance = TestBed.inject(PetService);

    spyOn(serviceInstance['_petTypesSubject'], 'next');

    serviceInstance.getAllPetTypes();
    const req = httpMock.expectOne(request => request.url === `${serviceInstance['_serverURL']}/pet-types`);
    req.flush(mockPetTypes);

    expect(serviceInstance['_petTypesSubject'].next).toHaveBeenCalledWith(mockPetTypes);
  });

});

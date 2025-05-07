import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { PhaserGame } from './phaser-game.component';
import { PetService } from '../app/services/pet.service';
import { ActivatedRoute } from '@angular/router';
import { config, of, Subject, Subscription } from 'rxjs'; // Import Subscription
import Phaser, { Scene } from 'phaser';
import { MyPet } from './scenes/MyPet';
import { EventBus } from './EventBus';
import { Pet } from '../app/models/pet';
import { Food } from '../app/models/food';

describe('PhaserGame', () => {
  let component: PhaserGame;
  let fixture: ComponentFixture<PhaserGame>;
  let petService: PetService;
  let route: ActivatedRoute;

  const mockPetService = {
    messages$: of({
        pet: {
            petId: 1, 
            petName: 'TestPet', 
            petAffectionLevel: 5, 
            petHungerLevel: 3, 
            petOwner: 'Owner',
            petType: { name: 'dog' },
            petXLocation: 0,
            petYLocation: 0,
            petDirection: 'right',
            petAction: 'idle',
            shared: 0
          } as Pet,
      food: [] as Food[],
      action: 'action',
      actionTime: 1000
    }),
    registerPetForViewing: jasmine.createSpy('registerPetForViewing').and.returnValue(of({})),
    connect: jasmine.createSpy('connect'),
    createPetFood: jasmine.createSpy('createPetFood'),
    petAPet: jasmine.createSpy('petAPet'),
    unsubscribeFromViewingPet: jasmine.createSpy('unsubscribeFromViewingPet')
  };
  const mockScene = jasmine.createSpyObj('MyPet', ['updatePet']);

  const mockActivatedRoute = {
    paramMap: of({
      get: (param: string) => (param === 'petId' ? '1' : '0')
    })
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PhaserGame],
      providers: [
        { provide: PetService, useValue: mockPetService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PhaserGame);
    component = fixture.componentInstance;
    petService = TestBed.inject(PetService);
    route = TestBed.inject(ActivatedRoute);
  });


  beforeEach(() => {
    fixture.detectChanges();
  });


  it('should create the Phaser game and initialize it correctly', () => {
    const mockSceneManager = {
      add: jasmine.createSpy('add').and.returnValue({}),
      start: jasmine.createSpy('start'),
    };
    const mockGameInstance = {
      scene: mockSceneManager,
      destroy: jasmine.createSpy('destroy'),
    } as unknown as Phaser.Game;

    const gameSpy = spyOn(Phaser, 'Game').and.returnValue(mockGameInstance);

    component.ngOnInit();

    expect(gameSpy).toHaveBeenCalledWith(
      jasmine.objectContaining({
        type: 0,
        parent: 'game-container',
        backgroundColor: '#D9D9D9', 
        scale: {
          mode: Phaser.Scale.RESIZE,
          autoCenter: Phaser.Scale.CENTER_BOTH
        },
        physics: {
          default: "arcade",
          arcade: {
            gravity: { x: 0, y: 500 },
            debug: false
          }
        }
      })
    );

    expect(mockSceneManager.add).toHaveBeenCalledWith('MyPet', MyPet, false);
    expect(mockSceneManager.start).toHaveBeenCalledWith('Boot', {
      createPetFood: jasmine.any(Function),
      petAPet: jasmine.any(Function),
    });

    expect(component.game).toBe(mockGameInstance);
  });


  it('should handle scene ready callback correctly', () => {
    const sceneReadySpy = spyOn(component, '_onSceneReady' as any).and.callThrough();
    spyOn(component, 'viewPet').and.stub();
    const mockScene = {};
    const mockStartGame = jasmine.createSpy('StartGame').and.returnValue({
      scene: {
        add: jasmine.createSpy('add').and.returnValue(mockScene),
        start: jasmine.createSpy('start'),
      },
      destroy: jasmine.createSpy('destroy'),
    });

    PhaserGame.prototype.game = mockStartGame() as any;
  
    component.ngOnInit();
  
    EventBus.emit('current-scene-ready', mockScene);
  
    expect(sceneReadySpy).toHaveBeenCalledWith(mockScene);
  });
  

  it('should handle receiving new pet data correctly', () => {
    const mockMyPet = new MyPet();
    const updatePetSpy = spyOn(mockMyPet, 'updatePet').and.stub();
  
    component.scene = mockMyPet;
  
    EventBus.emit('current-scene-ready', mockMyPet);
    
    component.messageSub = mockPetService.messages$.subscribe((data) => {
      const pet = data.pet as Pet;
      const foodList = data.food as Food[];
      const action = data.action;
      const actionTime = data.actionTime;
  
      (component.scene as MyPet).updatePet(pet, foodList, action, actionTime);
    });
  
    mockPetService.messages$.subscribe((data) => {
      expect(updatePetSpy).toHaveBeenCalledWith(data.pet, data.food, data.action, data.actionTime);
    });
  });


  it('should call registerPetForViewing on ngOnInit', () => {
    component.ngOnInit();
    expect(mockPetService.registerPetForViewing).toHaveBeenCalledWith(1);
  });


  it('should clean up on ngOnDestroy', () => {
    component.messageSub = new Subscription();
    const unsubscribeSpy = spyOn(component.messageSub, 'unsubscribe').and.callThrough();
    spyOn(Phaser.Game.prototype, 'destroy').and.stub();
  
    component.ngOnDestroy();
  
    expect(unsubscribeSpy).toHaveBeenCalled();
    expect(Phaser.Game.prototype.destroy).toHaveBeenCalled();
    expect(mockPetService.unsubscribeFromViewingPet).toHaveBeenCalled();
  });
});

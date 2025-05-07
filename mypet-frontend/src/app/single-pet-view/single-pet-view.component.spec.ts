import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SinglePetViewComponent } from './single-pet-view.component';
import { PhaserGame } from '../../game/phaser-game.component';
import { By } from '@angular/platform-browser';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { PetService } from '../services/pet.service';

describe('SinglePetViewComponent', () => {
  let component: SinglePetViewComponent;
  let fixture: ComponentFixture<SinglePetViewComponent>;
  let router: Router;

  const mockPetService = {
    unsubscribeFromViewingPet: jasmine.createSpy('unsubscribeFromViewingPet').and.callFake(() => {})
  };

  const mockActivatedRoute = {
    snapshot: { params: { petId: 1 } },
    paramMap: of({
      get: () => '1'
    })
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SinglePetViewComponent
      ],
      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: PetService, useValue: mockPetService },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SinglePetViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);

    spyOn(Phaser.Game.prototype, 'destroy').and.callFake(() => {});
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    fixture.destroy();
  });

  it('should create and render the PhaserGame component', () => {
    expect(component.phaserRef).toBeTruthy();
    const phaserElement = fixture.debugElement.query(By.directive(PhaserGame));
    expect(phaserElement).toBeTruthy();
    fixture.destroy();
  });
});


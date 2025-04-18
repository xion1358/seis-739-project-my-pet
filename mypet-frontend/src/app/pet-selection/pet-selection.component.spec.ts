import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PetSelectionComponent } from './pet-selection.component';

describe('PetSelectionComponent', () => {
  let component: PetSelectionComponent;
  let fixture: ComponentFixture<PetSelectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PetSelectionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PetSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

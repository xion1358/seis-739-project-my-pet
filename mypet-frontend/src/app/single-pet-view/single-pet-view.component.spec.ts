import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SinglePetViewComponent } from './single-pet-view.component';

describe('SinglePetViewComponent', () => {
  let component: SinglePetViewComponent;
  let fixture: ComponentFixture<SinglePetViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SinglePetViewComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SinglePetViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

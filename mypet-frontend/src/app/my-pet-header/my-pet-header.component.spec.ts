import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyPetHeaderComponent } from './my-pet-header.component';

describe('MyPetHeaderComponent', () => {
  let component: MyPetHeaderComponent;
  let fixture: ComponentFixture<MyPetHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyPetHeaderComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MyPetHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

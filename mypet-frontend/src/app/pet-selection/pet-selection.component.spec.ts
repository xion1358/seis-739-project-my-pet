import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PetSelectionComponent } from './pet-selection.component';
import { PetService } from '../services/pet.service';
import { Router, RouterModule, Routes } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { By } from '@angular/platform-browser';
import { MyPageComponent } from '../my-page/my-page.component';

describe('PetSelectionComponent', () => {
  let component: PetSelectionComponent;
  let fixture: ComponentFixture<PetSelectionComponent>;
  let petService: jasmine.SpyObj<PetService>;
  let router: jasmine.SpyObj<Router>;
  let petTypes$ = new BehaviorSubject([{ name: 'cat' }, { name: 'dog' }]);
  const routes: Routes = [
        { path: 'mypage', component: MyPageComponent},
      ];

  beforeEach(async () => {
    petService = jasmine.createSpyObj('PetService', ['getAllPetTypes', 'requestPetForOwner'], {
      petTypes$: petTypes$
    });

    router = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [PetSelectionComponent, RouterModule.forRoot(routes)],
      providers: [
        { provide: PetService, useValue: petService },
        { provide: Router, useValue: router }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PetSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display pet options', () => {
    const petOptions = fixture.debugElement.queryAll(By.css('.pet-option'));
    expect(petOptions.length).toBe(2);
  });

  it('should update selected pet type when a pet is clicked', () => {
    const petOption = fixture.debugElement.queryAll(By.css('.pet-option'))[0];
    petOption.triggerEventHandler('click', null);
    fixture.detectChanges();
    
    expect(component.selectedPetType).toBe('cat');
  });

  it('should bind pet name to input field', () => {
    const inputElement = fixture.debugElement.query(By.css('input')).nativeElement;
    inputElement.value = 'Buddy';
    inputElement.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    
    expect(component.petName).toBe('Buddy');
  });

  it('should show alert if no pet is selected on confirm', () => {
    spyOn(window, 'alert');
    
    component.selectedPetType = '';
    component.confirmSelection();
    
    expect(window.alert).toHaveBeenCalledWith('Please select a pet!');
  });

  it('should call requestPetForOwner on confirm when pet is selected', () => {
    component.selectedPetType = 'cat';
    component.petName = 'Buddy';
    
    component.confirmSelection();
    
    expect(petService.requestPetForOwner).toHaveBeenCalledWith('Buddy', 'cat');
  });

  it('should navigate to /mypage when "Go Back" is clicked', () => {
    const goBackButton = fixture.debugElement.query(By.css('.go-back-button'));
    goBackButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    
    expect(router.navigate).toHaveBeenCalledWith(['/mypage']);
  });
});

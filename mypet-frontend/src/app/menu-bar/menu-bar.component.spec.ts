import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MenuBarComponent } from './menu-bar.component';
import { RouterModule, Routes, Router } from '@angular/router';
import { Location } from '@angular/common';
import { Component } from '@angular/core';

@Component({ template: '' })
class MyPageComponent {}

@Component({ template: '' })
class CatalogComponent {}

describe('MenuBarComponent', () => {
  let component: MenuBarComponent;
  let fixture: ComponentFixture<MenuBarComponent>;
  let compiled: HTMLElement;
  let location: Location;
  let router: Router;

  const routes: Routes = [
    { path: 'mypage', component: MyPageComponent },
    { path: 'catalog', component: CatalogComponent },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterModule.forRoot(routes)],
    }).compileComponents();

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);

    fixture = TestBed.createComponent(MenuBarComponent);
    component = fixture.componentInstance;
    compiled = fixture.nativeElement;

    router.initialNavigation();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render menu links', () => {
    const menuLinks = compiled.querySelectorAll('a.menu-button');
    expect(menuLinks.length).toBe(2);
    expect(menuLinks[0].textContent).toContain('My Pets');
    expect(menuLinks[1].textContent).toContain('Pet Catalog');
  });

  it('should navigate to "/mypage" when clicking "My Pets"', async () => {
    const myPetsLink = compiled.querySelector('a[href="/mypage"]') as HTMLAnchorElement;
    myPetsLink.click();
    await fixture.whenStable();
    expect(location.path()).toBe('/mypage');
  });

  it('should navigate to "/catalog" when clicking "Pet Catalog"', async () => {
    const catalogLink = compiled.querySelector('a[href="/catalog"]') as HTMLAnchorElement;
    catalogLink.click();
    await fixture.whenStable();
    expect(location.path()).toBe('/catalog');
  });
});

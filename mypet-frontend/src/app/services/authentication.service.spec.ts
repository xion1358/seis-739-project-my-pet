import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { AuthenticationService } from './authentication.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router, RouterModule, Routes } from '@angular/router';
import { of, throwError } from 'rxjs';
import { MyPageComponent } from '../my-page/my-page.component';
import { HttpErrorResponse } from '@angular/common/http';

describe('AuthenticationService', () => {
  let service: AuthenticationService;
  let httpMock: HttpTestingController;
  let routerMock: jasmine.SpyObj<Router>;
  let router: jasmine.SpyObj<Router>;

  const routes: Routes = [
          { path: 'mypage', component: MyPageComponent},
        ];

  beforeEach(async () => {
    router = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterModule.forRoot(routes)],
      providers: [
        AuthenticationService,
        { provide: Router, useValue: router }
      ]
    })
    .compileComponents();

    service = TestBed.inject(AuthenticationService);
    httpMock = TestBed.inject(HttpTestingController);
    routerMock = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    localStorage.clear();
    sessionStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // Test login method (success)
  it('should login successfully', () => {
    const loginData = ['user1', 'password123'];
    const response = { username: 'user1', token: 'some-token' };
    spyOn(service, 'saveData').and.callThrough();

    service.login(loginData);

    const req = httpMock.expectOne(`${service['_serverURL']}/login`);
    expect(req.request.method).toBe('POST');
    req.flush(response);

    expect(service.saveData).toHaveBeenCalledWith(response);
    expect(service.getLoggedStatus()).toBeTrue();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/mypage']);
  });

  it('should handle login error', () => {
    const loginData = ['user1', 'wrongpassword'];
  
    spyOn(window, 'alert');
  
    // Arrange: Spy on the HTTP POST and return the error observable
    spyOn(service['_http'], 'post').and.returnValue(
      throwError(() => new HttpErrorResponse({
        error: [
          'Invalid credentials.',
        ],
        status: 400,
        statusText: 'Bad Request'
      }))
    );
  
    // Act: Call the login method
    service.login(loginData);
  
    // Assert: Check the alert message
    expect(window.alert).toHaveBeenCalledWith('Invalid input: \nInvalid credentials.\n');
  });
  
  
  it('should register successfully', () => {
    const registrationData = ['user1', 'User One', 'user1@example.com', 'password123'];
    const response = { username: 'user1', token: 'some-token' };
    spyOn(service, 'saveData').and.callThrough();

    service.registration(registrationData);

    const req = httpMock.expectOne(`${service['_serverURL']}/registration`);
    expect(req.request.method).toBe('POST');
    req.flush(response);

    expect(service.saveData).toHaveBeenCalledWith(response);
    expect(service.getLoggedStatus()).toBeTrue();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/mypage']);
  });

  it('should handle registration error', () => {
    const registrationData = ['user1', 'User One', 'user1@example.com', 'password123'];
  
    spyOn(window, 'alert');
  
    spyOn(service['_http'], 'post').and.returnValue(
      throwError(() => new HttpErrorResponse({
        error: [
          'Username already taken.'
        ],
        status: 400,
        statusText: 'Bad Request'
      }))
    );
  
    service.registration(registrationData);
  
    expect(window.alert).toHaveBeenCalledWith('Invalid input: \nUsername already taken.\n');
  });
  
  it('should validate login and navigate to mypage', fakeAsync(() => {
    const token = 'valid-token';
    localStorage.setItem('token', token);
  
    service['loggedInStatus'].next(true);
  
    spyOn(sessionStorage, 'getItem').and.callFake((key: string) => {
      if (key === 'lastNavigatedRoute') {
        return '/';
      }
      return null;
    });

    spyOn(service['_http'], 'post').and.returnValue(of({}));
  
    service.validateLogin();
  
    tick();
  
    expect(routerMock.navigate).toHaveBeenCalledWith(['/mypage']);
  }));

  it('should validate login and navigate to lastNavigatedRoute if length > 1', fakeAsync(() => {
    const token = 'valid-token';
    localStorage.setItem('token', token);
  
    service['loggedInStatus'].next(true);
  
    spyOn(sessionStorage, 'getItem').and.callFake((key: string) => {
      if (key === 'lastNavigatedRoute') {
        return '/dashboard';
      }
      return null;
    });
  
    spyOn(service['_http'], 'post').and.returnValue(of({}));
  
    service.validateLogin();
  
    tick();
  
    expect(routerMock.navigate).toHaveBeenCalledWith(['/dashboard']);
  }));

  it('should log off and clear data', () => {
    localStorage.setItem('token', 'valid-token');
    spyOn(service, 'deleteData').and.callThrough();

    service.logoff();

    expect(service.getLoggedStatus()).toBeFalse();
    expect(service.deleteData).toHaveBeenCalled();
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('should save data to localStorage', () => {
    const data = { username: 'user1', token: 'some-token' };
    service.saveData(data);

    expect(localStorage.getItem('username')).toBe('user1');
    expect(localStorage.getItem('token')).toBe('some-token');
  });

  it('should delete data from localStorage', () => {
    service.deleteData();

    expect(localStorage.getItem('username')).toBeNull();
    expect(localStorage.getItem('token')).toBeNull();
  });

  afterEach(() => {
    httpMock.verify();
  });
});

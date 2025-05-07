import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { Utility } from '../Utilities/Utility';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private _serverURL = environment.serverURL;
  private loggedInStatus = new BehaviorSubject<boolean>(false);
  loggedInStatus$ = this.loggedInStatus.asObservable();

  constructor(
    private _http: HttpClient, 
    private _router: Router) { 

    this.loggedInStatus.next(!!localStorage.getItem("token"));
  }

  /**
   * Initializes login authentication in server side through an api call
   */
  public login(data: string[]): void {

    const body = {username: data[0], password: data[1]};

    this._http.post(this._serverURL + "/login", body)
    .subscribe({
      next: (res: any) => {
        this.saveData(res);

        this.loggedInStatus.next(true);
        this._router.navigate(['/mypage']);
      },
      error: (errorResponse: HttpErrorResponse) => {
        let errorMessage = "Error logging in. Please try again.";
  
        if (errorResponse.error && Array.isArray(errorResponse.error)) {
          let validationErrorMessages = 'Invalid input: \n';
          errorResponse.error.forEach((message: string) => {
            validationErrorMessages += `${message}\n`;
          });
          errorMessage = validationErrorMessages;
        }
  
        alert(errorMessage);
      }
    });
  }

  /**
   * Initializes registration in server side through an api call
   */
  public registration(data: string[]): void {
    const body = { username: data[0], displayName: data[1], email: data[2], password: data[3] };

    this._http.post(this._serverURL + "/registration", body)
      .subscribe({
        next: (res: any) => {
          this.saveData(res);
          this.loggedInStatus.next(true);
          this._router.navigate(['/mypage']);
        },
        error: (errorResponse: HttpErrorResponse) => {
          let errorMessage = "Error registering. Please try again.";

          if (errorResponse.error && Array.isArray(errorResponse.error)) {
            let validationErrorMessages = 'Invalid input: \n';
            errorResponse.error.forEach((message: string) => {
              validationErrorMessages += `${message}\n`;
            });
            errorMessage = validationErrorMessages;

          } else if (errorResponse.status === 400 && errorResponse.error && typeof errorResponse.error === 'object') {
              errorMessage = `Error registering: ${errorResponse.error.message}`;

          } else if (errorResponse.status === 500) {
              errorMessage = "A server error occurred. Please try again later.";

          } else if (errorResponse.status === 409 && errorResponse.error?.message) {
            errorMessage = errorResponse.error.message;
          }

          alert(errorMessage);
        }
      });

  }

  /**
   * Initializes rechecking the token with the server and updating the login status in front-end
   */
  public validateLogin() {
    try {
      if (this.loggedInStatus.value) {
        const headers = { headers: Utility.getTokenHeader() };

        this._http.post(this._serverURL + "/validate", null, headers)
          .subscribe({
            next: () =>
            {
              if (sessionStorage.getItem('lastNavigatedRoute')?.length === 1)
              {
                this._router.navigate(['/mypage']);
              } else
              {
                this._router.navigate([sessionStorage.getItem('lastNavigatedRoute')]);
              }
            },
            error: () =>
            {
              alert("You've been logged off. Please sign in again.");
              this.logoff();
              this._router.navigate(['/log-in']);
            }
          });
      }
    } catch (error: any) {
      console.error("Encountered error while trying to validate login: ", error.message);
      this._router.navigate(['/']);
    }
    
  }

  /**
   * Does logoff actions
   */
  public logoff(){
    this.deleteData();
    this.loggedInStatus.next(false);
  }

  public saveData(data: any): void {
    localStorage.setItem("username", data.username);
    localStorage.setItem("token", data.token);
  }

  public deleteData(): void {
    localStorage.removeItem("username");
    localStorage.removeItem("token");
  }

  public getLoggedStatus(): boolean {
    return this.loggedInStatus.getValue();
  }

  
}

import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private _serverURL = environment.serverURL;
  // private _headers = new HttpHeaders({
  //   authorization: 'Basic ' + localStorage.getItem("token")
  // });
  private loggedInStatus = new BehaviorSubject<boolean>(false);
  loggedInStatus$ = this.loggedInStatus.asObservable();

  constructor(private _http: HttpClient, private _router: Router) { }

  /**
   * Initializes login authentication in server side through an api call
   */
  public login(data: string[]): void {

    const body = {username: data[0], password: data[1]};

    this._http.post(this._serverURL + "/login", body) // use for other requests {headers: this._headers}
    .subscribe({
      next: (res: any) => {
        this.saveData(res);

        this.loggedInStatus.next(true);
        this._router.navigate(['/mypage']);
      },
      error: () => {
        alert("Error logging in. Please try again");
      }
    });
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

  public getLoggedStatus(): Observable<boolean> {
    return this.loggedInStatus.asObservable();
  }

  
}

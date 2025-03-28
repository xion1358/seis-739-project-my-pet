import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-my-page',
  standalone: true,
  imports: [],
  templateUrl: './my-page.component.html',
  styleUrl: './my-page.component.css'
})
export class MyPageComponent {
  private _serverURL = environment.serverURL;

  constructor(private _http: HttpClient) {}

  // public sendHelloRequest() {
  //   const body = {name: "John"};
  //   const header = new HttpHeaders({
  //     authorization: 'Bearer ' + localStorage.getItem("token")
  //   })

  //   console.log("Sending the header with token: " + localStorage.getItem("token"));

  //   this._http.post(this._serverURL + "/hello", body, {headers: header}) // use for other requests {headers: this._headers}
  //   .subscribe({
  //     next: (res: any) => {
  //       console.log("Got response back");
  //       console.log("Got back: " + res.message);
  //     },
  //     error: (err) => {
  //       alert("Error saying hello: " + err.message);
  //     }
  //   });
  // }
}

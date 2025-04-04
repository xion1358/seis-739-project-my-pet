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

}

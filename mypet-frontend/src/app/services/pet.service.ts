import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { Utility } from '../Utilities/Utility';
import { Pet } from '../models/pet';
import { Observable, Subject } from 'rxjs';
import { Client, Message } from '@stomp/stompjs';
import { PetTypes } from '../models/pettypes';
import { SharedPetsResponse } from '../models/shared-pets-response';

@Injectable({
  providedIn: 'root'
})
export class PetService {
  private _serverURL = environment.serverURL;
  private _stompClient: Client;
  private _messageSubject = new Subject<any>();
  private _petTypesSubject = new Subject<any>();

  public messages$ = this._messageSubject.asObservable();
  public petTypes$ = this._petTypesSubject.asObservable();

  constructor(
    private _http: HttpClient, 
    private _router: Router) { }

  public queryForPets(): Observable<Pet[]>
  {
    try {
      if (Utility.getTokenHeader() && Utility.getUserName()) {
        const headers = Utility.getTokenHeader();
        const params = new HttpParams().set('owner', Utility.getUserName());

        return this._http.get<Pet[]>(this._serverURL + "/get-pets", {headers: headers, params: params});
      }
    } catch (error: any) {
      console.error("Encountered error while trying to query pets: ", error.message);
      this._router.navigate(['/']);
    }

    return new Observable<Pet[]>();
  }

  public registerPetForViewing(petId: number): Observable<Pet> {
    const headers = Utility.getTokenHeader();
    const params = new HttpParams()
      .set('owner', Utility.getUserName())
      .set('id', petId);
  
    return this._http.post<Pet>(this._serverURL + "/register-pet-for-viewing", null, { headers, params });
  }
  

  public connect(petId: number, shared: number): void {
    try {
      const token = localStorage.getItem('token');

      this._stompClient = new Client({
        brokerURL: environment.webSocketURL,
        connectHeaders: {
          'Authorization': 'Bearer ' + token
        },
        reconnectDelay: 5000,
        onConnect: () =>
        {
          //console.log("Connected to WebSocket");
          this.subscribeToPet(petId, shared);
        },
        onStompError: (frame) => {
          console.error('STOMP Error:', frame);
          this._stompClient.activate();
        }
      });

      this._stompClient.activate();
    } catch (error: any) {
      console.error("Error trying to connect to websocket: ", error.message);
    }
  }

  public subscribeToPet(petId: number, shared: number): void {
    let topic = (shared > 0) ? `/topic/shared/pet/` : `/topic/pet/`;

    this._stompClient.subscribe(`${topic}${petId}`, (message: Message) => {
      if (message.body === 'CLOSE') {
        this._stompClient.deactivate();
        alert("This pet has been unshared. Navigating back to mypage");
        this._router.navigate(['/mypage']);
      } else {
        const petData = JSON.parse(message.body);
        this._messageSubject.next(petData);
      }
      
    });
    
  }

  public unsubscribeFromViewingPet(): void {
    this._stompClient.deactivate();
  }

  public createPetFood(petId: number, foodType: string): void {
    const headers = Utility.getTokenHeader();
    const params = new HttpParams().set('id', petId).set('food', foodType);
  
    this._http.post<boolean>(this._serverURL + "/create-pet-food", null, { headers, params })
        .subscribe({
            next: (response) => {
                //console.log('Food created successfully:', response);
            },
            error: (err) => {
                console.error('Error creating food:', err);
            }
        });
  }

  public petAPet(petId: number): void {
    const headers = Utility.getTokenHeader();
    const params = new HttpParams().set('id', petId);
  
    this._http.post<boolean>(this._serverURL + "/pet-a-pet", null, { headers, params })
    .subscribe({
        next: (response) => {
            //console.log('Pet loved your pet!');
        },
        error: (err) => {
            console.error('Error trying to pet a pet:', err);
        }
    });
  }

  public getAllPetTypes(): void {
    const headers = Utility.getTokenHeader();
  
    this._http.post<PetTypes[]>(this._serverURL + "/pet-types", null, { headers })
    .subscribe({
        next: (response: PetTypes[]) => {
          this._petTypesSubject.next(response);
        },
        error: (err) => {
            console.error('Error trying to pet pet types:', err);
        }
    });
  }

  public requestPetForOwner(petName: string, petType: string): void {
    const headers = Utility.getTokenHeader();
    const params = new HttpParams()
      .set('owner', Utility.getUserName())
      .set('petName', petName)
      .set('petType', petType);
  
    this._http.post<boolean>(this._serverURL + "/request-a-pet", null, { headers, params })
    .subscribe({
        next: (response) => {
            if (response) {
              this._router.navigate(['/mypage']);
            } else {
              alert("Sorry, you have too many pets! Please let a pet go before requesting another one!");
            }
        },
        error: (err) => {
            console.error('Error trying to request a pet', err);
        }
    });
  }

  public abandonThisPet(petId: number): Observable<boolean> {
    const headers = Utility.getTokenHeader();
    const params = new HttpParams()
      .set('owner', Utility.getUserName())
      .set('petId', petId);
  
    return this._http.post<boolean>(this._serverURL + "/abandon-pet", null, { headers, params });
  }

  public queryForSharedPets(cursor: number, pageDirection: string): Observable<SharedPetsResponse> {
    try {
      if (Utility.getTokenHeader() && Utility.getUserName()) {
        const headers = Utility.getTokenHeader();
        const params = new HttpParams().set('cursor', cursor).set('direction', pageDirection);

        return this._http.get<SharedPetsResponse>(this._serverURL + "/get-shared-pets", {headers: headers, params: params});
      }
    } catch (error: any) {
      console.error("Encountered error while trying to query pets: ", error.message);
    }

    return new Observable<SharedPetsResponse>();
  }

  public shareThisPet(petId: number): Observable<boolean> {
    const headers = Utility.getTokenHeader();
    const params = new HttpParams()
      .set('owner', Utility.getUserName())
      .set('petId', petId);
  
    return this._http.post<boolean>(this._serverURL + "/share-pet", null, { headers, params });
  }

  public unshareThisPet(petId: number): Observable<boolean> {
    const headers = Utility.getTokenHeader();
    const params = new HttpParams()
      .set('owner', Utility.getUserName())
      .set('petId', petId);
  
    return this._http.post<boolean>(this._serverURL + "/unshare-pet", null, { headers, params });
  }

  // For testing purposes
  public getStompClient(): Client {
    return this._stompClient;
  }
}

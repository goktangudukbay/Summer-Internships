import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})

export class ApiService {
  
  baseUri:string = 'http://localhost:4000/api';
  headers = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private http: HttpClient) { }

  // Create User
  createUser(data, token): Observable<any> {
    let url = `${this.baseUri}/`;
    var reqHeader = new HttpHeaders({ 
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
   });
    return this.http.post(url, data, {headers: reqHeader})
      .pipe(
        catchError(this.errorMgmt)
      )
  }

  // Get all Users
  getUsers(token) {
    let url = `${this.baseUri}/`;
    var reqHeader = new HttpHeaders({ 
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
   });
    return this.http.get(url, {headers: reqHeader})
    .pipe(
      catchError(this.errorMgmt)
    )
  }

  //Get one User (For Update)
  getUser(token, username){
    let url = `${this.baseUri}/${username}`;
    var reqHeader = new HttpHeaders({ 
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
   });
   return this.http.get(url, {headers: reqHeader}).pipe(
    catchError(this.errorMgmt)
   )
  }

  // (login method)
  login(data): Observable<any> {
    let url = `${this.baseUri}/login`;
    return this.http.post(url, data).pipe(
      map((res: Response) => {
        return res || {}
      }),
      catchError(this.errorMgmt)
    )
  }

  // Update user
  updateUser(data, token): Observable<any> {
    let url = `${this.baseUri}/`;
    var reqHeader = new HttpHeaders({ 
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
   });
    return this.http.put(url, data, { headers: reqHeader }).pipe(
      catchError(this.errorMgmt)
    )
  }

  // Delete user
  deleteUser(username, token): Observable<any> {
    let url = `${this.baseUri}/${username}`;
    var reqHeader = new HttpHeaders({ 
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
   });


    return this.http.delete(url, { headers: reqHeader }).pipe(
      catchError(this.errorMgmt)
    )
  }

  // Send Message
  sendMessage(data, token): Observable<any> {
    let url = `${this.baseUri}/sendMessage`;
    var reqHeader = new HttpHeaders({ 
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
   });

   console.log(token);
   console.log(data);
   return this.http.post(url, data, {headers: reqHeader}).pipe(
    catchError(this.errorMgmt)
   )
  }

  // Read Inbox
  readInbox(token, username): Observable<any> {
    let url = `${this.baseUri}/inbox/${username}`;    
    var reqHeader = new HttpHeaders({ 
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
   });
   return this.http.get(url, {headers: reqHeader}).pipe(
    catchError(this.errorMgmt)
   )
  }

  // Read Inbox
  readOutbox(token, username): Observable<any> {
   let url = `${this.baseUri}/outbox/${username}`;   
    var reqHeader = new HttpHeaders({ 
      'Content-Type': 'application/json',
     'Authorization': 'Bearer ' + token
   });
    return this.http.get(url, {headers: reqHeader}).pipe(
      catchError(this.errorMgmt)
     )
  }


  //create new access
  newAccess(data, token): Observable<any> {
    console.log("a");
    console.log(data);
    console.log(token);
    let url = `${this.baseUri}/log`;
    var reqHeader = new HttpHeaders({ 
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
   });
   console.log(url);
   console.log(reqHeader);

    return this.http.post(url, data, {headers: reqHeader})
      .pipe(
        catchError(this.errorMgmt)
      )
  }


  //get access log
  listAccess(token): Observable<any> {
    let url = `${this.baseUri}/log`;
    var reqHeader = new HttpHeaders({ 
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
   });
    return this.http.get(url, {headers: reqHeader})
      .pipe(
        catchError(this.errorMgmt)
      )
  }s


  // Error handling 
  errorMgmt(error: HttpErrorResponse) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      // Get client-side error
      errorMessage = error.error.message;
    } else {
      // Get server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.log(errorMessage);
    return throwError(errorMessage);
  }



}
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { ApiResponse } from '../../shared/ApiResponse';
import { Router } from '@angular/router';
import { NotificationServiceService } from '../notifService/notificationService.service';
import { ChatServiceService } from '../chatService/chatService.service';

@Injectable({
  providedIn: 'root'
})


export class AuthService {
  private apiUrl = 'http://localhost:8080/auth/api'

  constructor(private http: HttpClient, private router: Router, private notifService : NotificationServiceService, private chatService : ChatServiceService) { }

  login(credentials: { email: string, password: string }): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/login`,
      credentials
    );
  }


  register(data: { name: string; email: string; password: string; role: string }): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.apiUrl}/register`, data);
  }

  logout() {
    this.notifService.disconnect();
    this.chatService.disconnect();
    localStorage.removeItem('token');
    this.router.navigate(["/login"]);
  }

  isLoggedIn(): boolean {
    const token = localStorage.getItem('token');
    return !!token; // true si le token existe
  }


}

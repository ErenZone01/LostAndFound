import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserResponse } from '../../shared/UserResponse';
import { Observable } from 'rxjs';
import { PostResponse } from '../../shared/PostResponse';
import { MatchReponse } from '../../shared/MatchReponse';

@Injectable({
  providedIn: 'root'
})
export class DashboardServiceService {
  private userApi = "http://localhost:8080/user/api";
  private annonceApi = "http://localhost:8080/post/api";
  private matchApi = "http://localhost:8080/match/api";

  constructor(private http: HttpClient) { }
  
  getUser(): Observable<UserResponse> {
    const token = localStorage.getItem('token'); // ou sessionStorage
    return this.http.get<UserResponse>(`${this.userApi}/getProfile`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getAllPostById() : Observable<PostResponse[]> {
    const token = localStorage.getItem('token'); // ou sessionStorage
    return this.http.get<PostResponse[]>(`${this.annonceApi}/getAllPostById`,{
      headers: {
        Authorization : `Bearer ${token}`
      }
    });
  }

  getPostById() : Observable<PostResponse[]> {
    const token = localStorage.getItem('token'); // ou sessionStorage
    return this.http.get<PostResponse[]>(`${this.annonceApi}/getAllPostById`,{
      headers: {
        Authorization : `Bearer ${token}`
      }
    });
  }

  getMatchs() : Observable<MatchReponse[]> {
    const token = localStorage.getItem('token'); // ou sessionStorage
    return this.http.get<MatchReponse[]>(`${this.matchApi}/getMatchs`,{
      headers: {
        Authorization : `Bearer ${token}`
      }
    });
  }

  getMatch() : Observable<MatchReponse> {
    const token = localStorage.getItem('token'); // ou sessionStorage
    return this.http.get<MatchReponse>(`${this.matchApi}/getMatch`,{
      headers: {
        Authorization : `Bearer ${token}`
      }
    });
  }

}

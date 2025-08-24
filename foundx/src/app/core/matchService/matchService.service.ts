import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatchReponse } from '../../shared/MatchReponse';
import { Observable } from 'rxjs';
import { UserResponse } from '../../shared/UserResponse';
import { PostResponse } from '../../shared/PostResponse';
import { State } from '../../shared/State';

@Injectable({
  providedIn: 'root'
})
export class MatchServiceService {
  private matchApi = "http://localhost:8080/match/api";
  private userApi = "http://localhost:8080/user/api"
  private token: string | null = "";

  constructor(private http: HttpClient) { }

  public getMatchsFromIdPost(idPost: string): Observable<MatchReponse[]> {
    this.token = localStorage.getItem("token");
    return this.http.post<MatchReponse[]>(`${this.matchApi}/getMatchsFromIdPost`, idPost, {
      headers: {
        Authorization: `Bearer ${this.token}`
      }
    })
  }

 


  public getMatchById(matchId: string): Observable<MatchReponse>{
    this.token = localStorage.getItem("token");
    return this.http.get<MatchReponse>(`${this.matchApi}/getMatchById`, {
      headers: {
        Authorization: `Bearer ${this.token}`
      },
      params: { "matchId": matchId}
    })
  }

  public getPostByMatchId(matchId: string): Observable<PostResponse[]> {
    this.token = localStorage.getItem("token");
    return this.http.get<PostResponse[]>(`${this.matchApi}/getPostByMatchId`, {
      headers: {
        Authorization: `Bearer ${this.token}`
      },
      params: { "matchId": matchId}
    })
  }


  public updateMatchState(matchId: string, state: State): Observable<string> {    
    this.token = localStorage.getItem("token");
    return this.http.put<string>(`${this.matchApi}/updateMatchState`, {}, {
      headers: {
        Authorization: `Bearer ${this.token}`
      },
      params: { "matchId": matchId, "postId":state.postId, "state":state.state }
    })
  }

   public updateMatchAndPostAcitve(matchId: string, state: State): Observable<string> {    
    this.token = localStorage.getItem("token");
    return this.http.put<string>(`${this.matchApi}/updateMatchAndPostActive`, {}, {
      headers: {
        Authorization: `Bearer ${this.token}`
      },
      params: { "matchId": matchId, "postId":state.postId}
    })
  }

  public getUserById(userId: string): Observable<UserResponse> {
    this.token = localStorage.getItem("token");
    return this.http.get<UserResponse>(`${this.userApi}/getUserById`, {
      headers: {
        Authorization: `Bearer ${this.token}`
      },
      params: { "userId": userId }
    })
  }

  public search(match: MatchReponse) {
    this.token = localStorage.getItem("token");

    return this.http.post<MatchReponse>(
      `${this.matchApi}/sort`,
      {}, // <-- le body (vide si pas nécessaire)
      {
        headers: {
          Authorization: `Bearer ${this.token}`
        },
        params: {
          keyword: match.keyword,
          category: match.category,
          lieu: match.lieu,
          date: match.date
        }
      }
    );
  }


}

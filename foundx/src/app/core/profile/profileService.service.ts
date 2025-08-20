import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserUpdate } from '../../shared/UserUpdate';

@Injectable({
  providedIn: 'root'
})
export class ProfileServiceService {

  private userApi = "http://localhost:8080/user/api";

  constructor(private http: HttpClient) { }

  public UpdateUser(user: UserUpdate) {
    const token = localStorage.getItem("token");
    return this.http.put<UserUpdate>(`${this.userApi}/update`, user, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  
  public DeleteUser() {
    const token = localStorage.getItem("token");
    return this.http.delete(`${this.userApi}/delete`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

}


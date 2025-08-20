import { Injectable } from '@angular/core';
import { PostResponse } from '../../shared/PostResponse';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PostServiceService {

constructor(private http : HttpClient) { }
  postApi : string = "http://localhost:8080/post/api";

  public CreatePost(data : FormData){
    const token = localStorage.getItem("token");
    return this.http.post<FormData>(`${this.postApi}/create`, data, {
      headers:{
        Authorization: `Bearer ${token}`
      }
    })
  }

}

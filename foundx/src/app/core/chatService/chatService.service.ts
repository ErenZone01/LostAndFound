import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ChatMessage } from '../../shared/ChatMessage';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { UserResponse } from '../../shared/UserResponse';

@Injectable({
  providedIn: 'root'
})
export class ChatServiceService {

  constructor(private http: HttpClient) { }

  getConversation(matchId: string, postId: string): Observable<ChatMessage[]> {
    const token = localStorage.getItem("token");
    return this.http.get<ChatMessage[]>(`http://localhost:8086/api/messages`, {
      params: { "matchId": matchId, "postId": postId },
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  private stompClient: Client | null = null;
  private chatSubscription: any;

  connect(userId : string, token: string, onMessage: (msg: ChatMessage) => void): void {
    if (this.stompClient?.connected) return;

    this.stompClient = new Client({
      // ✅ Utilisation de SockJS
      webSocketFactory: () => new SockJS("http://localhost:8086/ws"),
      connectHeaders: {
        Authorization: 'Bearer ' + token
      },
      debug: (str) => {
        console.log(str);
      },
      reconnectDelay: 5000, // ✅ reconnexion auto
    });

    this.stompClient.onConnect = () => {
      console.log("✅ Connecté au WebSocket");
      // écoute la queue user
      this.chatSubscription = this.stompClient?.subscribe(`/user/${userId}/chat`, (message: IMessage) => {
        const msg: ChatMessage = JSON.parse(message.body);
        console.log("📩 Message reçu:", msg);
        onMessage(msg);
      });

    };

    this.stompClient.onStompError = (frame) => {
      console.error('❌ STOMP error', frame);
    };

    this.stompClient.activate(); // ✅ démarre la connexion
  }

  send(destination: string, body: any) {
    this.stompClient?.publish({
      destination,
      body: JSON.stringify(body)
    });
  }

  disconnect() {
    this.stompClient?.deactivate();
    this.stompClient = null;
  }
}

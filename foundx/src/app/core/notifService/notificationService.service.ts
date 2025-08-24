import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { NotifReponse } from '../../shared/NotifReponse';

@Injectable({
  providedIn: 'root'
})
export class NotificationServiceService {

  constructor(private http: HttpClient) { }

  getNotification(userId: string): Observable<NotifReponse[]> {
    const token = localStorage.getItem("token");
    return this.http.get<NotifReponse[]>(`http://localhost:8085/api/getNotification`, {
      params: { "receiverId": userId },
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  private stompClient: Client | null = null;
  private chatSubscription: any;
  private connected = false; // ✅ évite les connexions multiples


  // updateNotif(notifId: string) {
  //   const token = localStorage.getItem("token");
  //   return this.http.put<String>(`http://localhost:8085/api/updateNotification`, {} ,{
  //     params: { "notifId": notifId },
  //     headers: { Authorization: `Bearer ${token}` }
  //   });
  // }

  connect(userId: string, token: string, onMessage: (msg: NotifReponse) => void): void {
    if (this.connected) {
      console.log("⚠️ Déjà connecté au WebSocket, on ignore.");
      return;
    }
    if (this.stompClient?.connected) return;

    this.stompClient = new Client({
      // ✅ Utilisation de SockJS
      webSocketFactory: () => new SockJS("http://localhost:8085/ws"),
      connectHeaders: {
        Authorization: 'Bearer ' + token
      },
      debug: (str) => {
        console.log(str);
      },
      reconnectDelay: 5000, // ✅ reconnexion auto
    });

    this.stompClient.onConnect = () => {
      this.connected = true;
      console.log("✅ Connecté au WebSocket");
      // écoute la queue user
      this.chatSubscription = this.stompClient?.subscribe(`/user/${userId}/notifications`, (message: IMessage) => {
        const msg: NotifReponse = JSON.parse(message.body);
        console.log("📩 notif reçu:", msg);
        onMessage(msg);
      });

    };

    this.stompClient.onStompError = (frame) => {
      console.error('❌ STOMP error', frame);
    };

    this.stompClient.activate(); // ✅ démarre la connexion
  }

  disconnect() {
    this.connected = false;
    this.stompClient?.deactivate();
    this.stompClient = null;
  }
}

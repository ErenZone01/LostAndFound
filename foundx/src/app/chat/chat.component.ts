import { Component, ViewChild, OnDestroy, OnInit, ElementRef } from '@angular/core';
import { Subscription } from 'rxjs';
import { ChatMessage } from '../shared/ChatMessage';
import { ChatServiceService } from '../core/chatService/chatService.service';
import { Client, Message, Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { DashboardServiceService } from '../core/dashboard/dashboardService.service';
import { UserResponse } from '../shared/UserResponse';
import { MatchServiceService } from '../core/matchService/matchService.service';
import { ActivatedRoute, Router, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-chat',
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, OnDestroy {
  @ViewChild('chatContainer') private chatContainer!: ElementRef;
  private chatSubscription: any;
  initial: string | undefined = "";
  user: UserResponse = { userId: null, email: null, name: null, role: null };
  receiver: UserResponse = { userId: null, email: null, name: null, role: null };
  private chatMessage: ChatMessage = { receiver: "", sender: "", content: "", type: "", matchId: "", postId: "" };
  chats: ChatMessage[] = new Array();
  newMessage: string = "";

  constructor(private dashboard: DashboardServiceService, private matchService: MatchServiceService, private route: ActivatedRoute, private chatservice: ChatServiceService) { }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    try {
      this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
    } catch (err) { }
  }

  sendMessage() {
    const receiverId = this.route.snapshot.paramMap.get("receiverId");
    const matchId = this.route.snapshot.paramMap.get("matchId");
    const postId = this.route.snapshot.paramMap.get("postId");
    if (this.newMessage.length == 0 || this.user.userId == "" || receiverId == null || matchId == null || postId == null) {
      console.log("erreur los de l'envoie");
      return
    }
    this.chatMessage = { sender: this.user.userId, receiver: receiverId, content: this.newMessage, type: "chat", matchId: matchId, postId: postId }
    this.chatservice.send('/app/chat', this.chatMessage);
    this.chats.push(this.chatMessage); // ajoute ton message localement
    this.newMessage = ""; // reset champ input
  }

  getUser() {
    this.dashboard.getUser().subscribe({
      next: (res: UserResponse) => {
        console.log("me: ", res);
        this.user = res;
        this.chatMessage.sender = this.user.userId;
      },
      error: (err) => {
        console.log(err);

      },
    })
  }
  getUserId(userId: string) {
    //recuperer le profile de l'utilisateur
    this.matchService.getUserById(userId).subscribe({
      next: (res: UserResponse) => {
        this.receiver = res;
        this.initial = this.receiver.name?.charAt(0).toString();
        console.log("receiver : ", this.receiver, " present");

      },
      error: (err) => {
        console.log(err);
      }
    })
  }

  getConversation(matchId: string, postId: string) {
    this.chatservice.getConversation(matchId, postId).subscribe({
      next: (history: ChatMessage[]) => {
        this.chats = history;
      },
      error: (err) => console.error(err)
    });

  }


  ngOnInit(): void {
    this.dashboard.getUser().subscribe({
      next: (res: UserResponse) => {
        console.log("me: ", res);
        this.user = res;
        this.chatMessage.sender = this.user.userId;
        const receiverId = this.route.snapshot.paramMap.get("receiverId");
        const matchId = this.route.snapshot.paramMap.get("matchId");
        const postId = this.route.snapshot.paramMap.get("postId");


        console.log("receiverId", receiverId);

        if (receiverId && matchId && postId && this.user.userId != null) {
          //recevoir les conversations
          this.getConversation(matchId, postId);
          //recevoir le receveur
          this.getUserId(receiverId);
          //recuperer le token
          const token = localStorage.getItem("token");
          //se connecter au websocket
          if (token != null && this.user != null) {
            this.chatservice.connect(this.user.userId, token, (msg: ChatMessage) => {
              this.chats.push(msg);
            });
          }
        }
      },
      error: (err) => {
        console.log(err);

      },
    })

  }

  ngOnDestroy(): void {
    if (this.chatSubscription) {
      this.chatSubscription.unsubscribe();
    }
    // if (this.socketClient) {
    //   this.socketClient.disconnect(() => {
    //     console.log("Disconnected from WS");
    //   });
    // }
    this.chatservice.disconnect()
  }

}

import { Component, signal } from '@angular/core';
import { NavigationEnd, Router, RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from './core/authService/auth.service';
import { CommonModule } from '@angular/common';
import { DashboardServiceService } from './core/dashboard/dashboardService.service';
import { UserResponse } from './shared/UserResponse';
import { NotifReponse } from './shared/NotifReponse';
import { NotificationServiceService } from './core/notifService/notificationService.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('foundx');

  notifications: NotifReponse[] = [];
  showPanel = false;
  isAuthenticated = false;
  initial : string | undefined = "";

  constructor(private router: Router, private notifService: NotificationServiceService, public authService: AuthService, public dashboardservice: DashboardServiceService) { }

  getUser() {
    this.dashboardservice.getUser().subscribe({
      next: (res: UserResponse) => {
        this.initial = res.name?.charAt(0).toString();
        this.notifService.getNotification(res.userId!).subscribe(n => {
          this.notifications = n;
        });
        //se connecter au websocket
        const token = localStorage.getItem("token");
        if (token != null && res.userId != null) {
          this.notifService.connect(res.userId, token, (notif: NotifReponse) => {
            this.notifications.push(notif);
          });
        }
      },
      error: (err) => {
        console.log("utilisateur non connecté");
      }
    })
  }

  ngOnInit(): void {
    // Vérifie au chargement
    this.checkAuthAndInit();

    // Écoute les changements de route
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.checkAuthAndInit();
    });
  }

  private initialized = false;

  private checkAuthAndInit() {
    this.isAuthenticated = this.authService.isLoggedIn();
    const currentUrl = this.router.url;

    if (this.isAuthenticated && !currentUrl.includes('/login') && !currentUrl.includes('/register')) {
      if (!this.initialized) {
        this.initialized = true;
        this.startNotifications();
      }
    }
  }


  private startNotifications() {
    // Ici tu déclenches ton système de notifications
    console.log("🔔 Notifications activées !");
    this.getUser();

  }


  togglePanel() {
    this.showPanel = !this.showPanel;
  }

  markAsRead(i: number) {
    console.log("lu");
    ;
  }
}

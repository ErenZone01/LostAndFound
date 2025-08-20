import { Component, signal } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from './core/authService/auth.service';
import { CommonModule } from '@angular/common';
import { DashboardServiceService } from './core/dashboard/dashboardService.service';
import { UserResponse } from './shared/UserResponse';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('foundx');
  user: UserResponse = { userId: null, email: null, name: null, role: null };

  constructor(public authService: AuthService, private dashboardservice : DashboardServiceService) { }

  getUser() {
    //recuperer le profile de l'utilisateur
    this.dashboardservice.getUser().subscribe({
      next: (res: UserResponse) => {
        this.user = res;
      },
      error: (err) => {
       console.log(err);       
      }
    })
  }

  ngOnInit(){
    this.getUser()
  }

}

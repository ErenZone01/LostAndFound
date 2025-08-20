import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/authService/auth.service';
import { ApiResponse } from '../../shared/ApiResponse';


@Component({
  selector: 'app-login',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  email = "";
  password = "";
  error: string | null = null;

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit(){
     if (this.authService.isLoggedIn()){
      this.router.navigate(['dashboard']);
     }
  }

  login() {
    if (this.email.length == 0) {
      this.error = "l'email ne peut etre vide";
      return;
    }
    if (this.password.length == 0) {
      this.error = "le password ne peut etre vide"
      return;
    }
    const credentials = { email: this.email, password: this.password };
    console.log(credentials);
    this.authService.login(credentials).subscribe({
      next: (res: ApiResponse) => {
        if (res.success) {
          // Connexion OK
          console.log("connexion reussi");
          localStorage.setItem("token", res.message);
          this.router.navigate(['dashboard']);
        } else {
          // Erreur côté serveur
          console.log("erreur")
          this.error = res.message;
        }
      },
      error: (err) => {
        // Erreur réseau / serveur down
        console.log(err);
        this.error = "Impossible de se connecter au serveur.";
      }
    });
  }

}


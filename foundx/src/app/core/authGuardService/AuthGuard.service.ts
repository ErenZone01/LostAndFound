import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService {


  constructor(private router: Router) {}

  canActivate(): boolean {
    const token = localStorage.getItem('token'); // on récupère le token

    if (token) {
      // Token présent → accès autorisé
      return true;
    } else {
      // Pas de token → redirection vers login
      this.router.navigate(['/login']);
      return false;
    }
  }

}

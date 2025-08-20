import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/authService/auth.service';
import { ApiResponse } from '../../shared/ApiResponse';


@Component({
  selector: 'app-register',
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class Register {

  name : string = ''
  email : string = ''
  password : string = ''
  confirmationMDP: string = ''
  role : string = ''
  error : string | null = null;

  constructor(private AuthService : AuthService, private router : Router){

  }

  ngOnInit(){
     if (this.AuthService.isLoggedIn()){
      this.router.navigate(['dashboard']);
     }
  }

  register(){
    if (this.name.trim().length == 0 || this.email.trim().length == 0 || this.password.trim().length == 0 || this.confirmationMDP.trim().length == 0 || this.role.trim().length == 0){
        this.error = "Veuillez remplir tous les champs !"
        return;
    }
    if (this.password != this.confirmationMDP){
        this.error = "La confirmation de mot de passe ne correspond pas !";
        return;
    }
    this.AuthService.register({name : this.name, email : this.email, password : this.password, role : this.role}).subscribe({
      next: (Res : ApiResponse) =>{
        if (Res.success){
          console.log(Res.message);
          this.router.navigate(['/login'])
        }else{
          this.error = Res.message;
        }
      },
      error: (err)=>{
        console.log(err);
        this.error = "Impossible de se connecter au serveur."

      }
    })

    
  
  }

}

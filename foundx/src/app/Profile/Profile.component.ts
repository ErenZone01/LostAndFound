import { Component, OnInit } from '@angular/core';
import { DashboardServiceService } from '../core/dashboard/dashboardService.service';
import { UserResponse } from '../shared/UserResponse';
import { FormsModule } from '@angular/forms';
import { UserUpdate } from '../shared/UserUpdate';
import { CommonModule } from '@angular/common';
import { ProfileServiceService } from '../core/profile/profileService.service';
import { AuthService } from '../core/authService/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-Profile',
  imports: [FormsModule, CommonModule],
  templateUrl: './Profile.component.html',
  styleUrls: ['./Profile.component.css']
})
export class ProfileComponent implements OnInit {
  password: string | null = "";
  confimPassword: string | null = "";
  error: string | null = null;
  name: string | null = "";
  email: string | null = "";
  role: string | null = "";
  user: UserResponse = { userId: null, name: null, email: null, role: null };
  newUser: UserUpdate = { name: null, email: null, password: null, role: null };
  showForm: boolean = false;

  openForm() {
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
  }


  constructor(private dashboardServie: DashboardServiceService, private profileService: ProfileServiceService, public authService: AuthService, private router : Router) { }

  updateUser() {
    if (this.password != this.confimPassword) {
      this.error = "la confirmation du password ne correspond pas au password";
      return;
    }
    this.newUser = { name: this.name, email: this.email, password: this.password, role: this.role };
    console.log(this.newUser);
    this.profileService.UpdateUser(this.newUser).subscribe({
      next: (res) => {
        console.log(res);
        this.getUser();
      },
      error: (err) => {
        console.log(err);

      }
    });
    this.closeForm();
  }

  deleteUser(){
    this.profileService.DeleteUser().subscribe({
      next:(res)=>{
        console.log(res);
        localStorage.removeItem("token");
        this.router.navigate(["/login"]);
      },
      error:(err)=>{
        console.log(err);
        
      }
    })
  }

  getUser() {
    this.dashboardServie.getUser().subscribe({
      next: (res: UserResponse) => {
        this.user = res;
        this.email = res.email;
        this.name = res.name;
        this.role = res.role;
      },
      error: (err) => {
        console.log(err);
      }
    })
  }

  ngOnInit() {
    this.getUser();
  }

}

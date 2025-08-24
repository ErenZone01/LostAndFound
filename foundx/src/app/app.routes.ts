import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Home } from './dashboard/home/home';
import { AuthGuardService } from './core/authGuardService/AuthGuard.service';
import { MatchComponent } from './match/match.component';
import { ProfileComponent } from './Profile/Profile.component';
import { AnnoncematchingComponent } from './annoncematching/annoncematching.component';
import { ChatComponent } from './chat/chat.component';

export const routes: Routes = [{ path: 'login', component: Login },
{ path: 'register', component: Register },
{ path: 'dashboard', component: Home, canActivate: [AuthGuardService] },
{ path: 'match/:id', component: MatchComponent, canActivate: [AuthGuardService] },
{ path: 'annoncematching/:id', component: AnnoncematchingComponent, canActivate: [AuthGuardService] },
{ path: 'profile', component: ProfileComponent, canActivate: [AuthGuardService] },
{ path: 'chat/:matchId/:postId/:receiverId', component: ChatComponent },
{ path: '', redirectTo: 'dashboard', pathMatch: 'full' }];

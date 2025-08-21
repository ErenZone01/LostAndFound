import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../core/authService/auth.service';
import { DashboardServiceService } from '../core/dashboard/dashboardService.service';
import { CommonModule } from '@angular/common';
import { MatchReponse } from '../shared/MatchReponse';
import { MatchServiceService } from '../core/matchService/matchService.service';
import { UserResponse } from '../shared/UserResponse';
import { State } from '../shared/State';

@Component({
  selector: 'app-match',
  imports: [CommonModule],
  templateUrl: './match.component.html',
  styleUrls: ['./match.component.css']
})
export class MatchComponent implements OnInit {

  annonceId!: string;
  error: string | null = "";
  matchs: MatchReponse[] = new Array();
  user: UserResponse[] = new Array();


  constructor(private route: ActivatedRoute, private matchService: MatchServiceService, private dashboardservice: DashboardServiceService) { }

  getMatchsFromIdPost(idPost: string) {
    this.matchService.getMatchsFromIdPost(idPost).subscribe({
      next: (res: MatchReponse[]) => {
        console.log("les match de getMatchFromIdPost : ", res);
        this.matchs = res;
        this.matchs.forEach((m) => {
          console.log("userId:", m.userId);
          this.getUserId(m.userId);
        })
      },
      error: (err) => {
        console.log("une erreur detecté : " + err);
      }
    })
  }

  getUserId(userId: string) {
    //recuperer le profile de l'utilisateur
    this.matchService.getUserById(userId).subscribe({
      next: (res: UserResponse) => {
        this.user.push(res);
        console.log("user : ", this.user, "ajouter");

      },
      error: (err) => {
        this.error = "undefined";
        console.log(err);

      }
    })
  }

  validateMatch(index: number, matchId: string) {
    this.annonceId = this.route.snapshot.paramMap.get('id')!;
    const etat = this.matchs[index].etat.map(e => {
      if (e.postId === this.annonceId) {
        return { ...e, state: true }; // crée un nouvel objet
      }
      return e;
    });

    // Remplace la référence entière de 'etat' pour forcer la détection
    this.matchs[index] = {
      ...this.matchs[index],
      etat: etat
    };

    this.updateMatchState(matchId, {
      postId: this.annonceId,
      state: true
    });
  }



  cancelMatch(index: number, matchId: string) {
    this.annonceId = this.route.snapshot.paramMap.get('id')!;
    const etat = this.matchs[index].etat.map(e => {
      if (e.postId === this.annonceId) {
        return { ...e, state: true }; // crée un nouvel objet
      }
      return e;
    });

    // Remplace la référence entière de 'etat' pour forcer la détection
    this.matchs[index] = {
      ...this.matchs[index],
      etat: etat
    };

    this.updateMatchState(matchId, {
      postId: this.annonceId,
      state: false
    });
  }

  openChat(index: number) {
    console.log('Ouvrir chat pour le match', this.matchs[index]);
    // ici tu peux rediriger vers une page chat ou ouvrir un modal
  }

  updateMatchState(matchId: string, state: State) {
    console.log("le state: ", state.state);

    this.matchService.updateMatchState(matchId, state).subscribe({
      next: (res: string) => {
        this.error = res
      },
      error: (err) => {
        this.annonceId = this.route.snapshot.paramMap.get('id')!;
        this.getMatchsFromIdPost(this.annonceId);
        console.log(err);

      }
    }) 
  }



  ngOnInit(): void {
    this.annonceId = this.route.snapshot.paramMap.get('id')!;
    console.log("annonce_id : " + this.annonceId);
    this.getMatchsFromIdPost(this.annonceId);
    // Exemple: appeler ton service pour récupérer les infos
    // this.annonceService.getAnnonceById(this.annonceId).subscribe(...)
  }

}

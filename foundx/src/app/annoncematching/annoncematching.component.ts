import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PostResponse } from '../shared/PostResponse';
import { MatchServiceService } from '../core/matchService/matchService.service';
import { CommonModule } from '@angular/common';
import { MatchReponse } from '../shared/MatchReponse';
import { State } from '../shared/State';

@Component({
  selector: 'app-annoncematching',
  imports: [CommonModule],
  templateUrl: './annoncematching.component.html',
  styleUrls: ['./annoncematching.component.css']
})
export class AnnoncematchingComponent implements OnInit {

  constructor(private route: ActivatedRoute, private matchService: MatchServiceService, private router: Router) { }
  matchId: string = "";
  posts: PostResponse[] = new Array();
  match: MatchReponse = { id: "", userId: "", keyword: "", type: "", lieu: "", date: "", description: [], category: "", matchedPostIds: [], active: "", createdAt: "", lastMatchDate: "", etat: new Array() };


  public OpenChat(post: PostResponse) {
    this.matchId = this.route.snapshot.paramMap.get('id')!;
    const receiver = post.userId;
    this.router.navigate([`/chat/${this.matchId}/${post.postId}/${receiver}`])
  }

  cancelMatch(index: number, postId: string) {
    this.matchId = this.route.snapshot.paramMap.get('id')!;
    const etat = this.match.etat.map(e => {
      if (e.postId === postId) {
        return { ...e, state: true }; // crée un nouvel objet
      }
      return e;
    });

    // Remplace la référence entière de 'etat' pour forcer la détection
    this.match = {
      ...this.match,
      etat: etat
    };

    this.updateMatchState(this.matchId, {
      postId: postId,
      state: false
    });
  }

  updateMatchState(matchId: string, state: State) {
    console.log("le state: ", state.state);
    this.matchId = this.route.snapshot.paramMap.get('id')!;

    this.matchService.updateMatchState(matchId, state).subscribe({
      next: (res: string) => {
        console.log(res);
      },
      error: (err) => {
        matchId = this.route.snapshot.paramMap.get('id')!;
        this.getMatchById(matchId);
        console.log(err);
      }
    })
  }

  getMatchById(matchId: string) {
    this.matchService.getMatchById(matchId).subscribe({
      next: (res: MatchReponse) => {
        this.match = res;
        console.log("match : ", res);

      },
      error: (err) => {
        console.log(err);

      }
    })
  }

  getPostByMatchId(matchId: string) {
    this.matchService.getPostByMatchId(matchId).subscribe({
      next: (res: PostResponse[]) => {
        this.posts = res;
        console.log("match extrait du match", res);

      },
      error: (err) => {
        console.log(err);

      }
    })
  }

  ngOnInit() {
    this.matchId = this.route.snapshot.paramMap.get('id')!;
    console.log("annonce_id : " + this.matchId);
    this.getPostByMatchId(this.matchId);
    this.getMatchById(this.matchId);
  }

}

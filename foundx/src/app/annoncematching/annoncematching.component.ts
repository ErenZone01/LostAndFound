import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PostResponse } from '../shared/PostResponse';
import { MatchServiceService } from '../core/matchService/matchService.service';
import { CommonModule } from '@angular/common';
import { MatchReponse } from '../shared/MatchReponse';

@Component({
  selector: 'app-annoncematching',
  imports: [CommonModule],
  templateUrl: './annoncematching.component.html',
  styleUrls: ['./annoncematching.component.css']
})
export class AnnoncematchingComponent implements OnInit {

  constructor(private route: ActivatedRoute, private matchService : MatchServiceService) { }
  matchId: string = "";
  posts : PostResponse[] = new Array();
    match: MatchReponse = {id : "", userId: "", keyword: "", type: "", lieu: "", date: "", description: [], category: "", matchedPostIds: [], active: "", createdAt: "", lastMatchDate: "", etat : new Array() };
  


  getMatchById(matchId : string){
    this.matchService.getMatchById(matchId).subscribe({
      next:(res : MatchReponse)=>{
        this.match = res;
        console.log("match : ", res);
        
      },
      error:(err)=>{
        console.log(err);
        
      }
    })
  }

  getPostByMatchId(matchId : string){
    this.matchService.getPostByMatchId(matchId).subscribe({
      next:(res : PostResponse[])=>{
        this.posts = res;
        console.log("match extrait du match", res);
        
      },
      error:(err)=>{
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

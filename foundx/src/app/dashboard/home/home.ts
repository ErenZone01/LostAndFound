import { Component } from '@angular/core';
import { UserResponse } from '../../shared/UserResponse';
import { DashboardServiceService } from '../../core/dashboard/dashboardService.service';
import { CommonModule } from '@angular/common';
import { PostResponse } from '../../shared/PostResponse';
import { RouterModule } from '@angular/router';
import { MatchReponse } from '../../shared/MatchReponse';
import { FormsModule } from '@angular/forms';
import { Global } from '../../shared/Global';
import { PostServiceService } from '../../core/postService/postService.service';
import { MatchServiceService } from '../../core/matchService/matchService.service';

@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {
  constructor(private dashboardservice: DashboardServiceService, public global: Global, public postService: PostServiceService, private matchService: MatchServiceService) { }
  private selectedFile: File | null = null;
  //Modal Post
  isModalPostOpen = false;
  isModalSearchOpen = false;


  modalPost: PostResponse = {
    postId: null,
    userId: null,
    type: null,
    lieu: null,
    date: null,
    imageId: null,
    imageBase64: null,
    description: [''],
    category: null,
    active: null
  };

  openModal(choice: string) {
    if (choice == "post") {
      this.isModalPostOpen = true;
    } else {
      this.isModalSearchOpen = true
    }
  }

  closeModal(choice: string) {
    if (choice == "post") {
      this.isModalPostOpen = false;
    } else {
      this.isModalSearchOpen = false;
    }
  }

  cancelModal(choice: string) {
    // réinitialiser les champs si nécessaire
    this.modalPost = {
      postId: null,
      userId: null,
      type: null,
      lieu: null,
      date: null,
      imageId: null,
      imageBase64: null,
      description: [''],
      category: null,
      active: null
    };
    this.closeModal(choice);
  }

  createPost() {
    const formData = new FormData();
    // Ajout du fichier sous le champ "image"
    if (this.selectedFile) {
      formData.append("image", this.selectedFile);
    }
    // Ajout de l'objet modalPost sous le champ "post"
    // ⚠️ Il faut le convertir en JSON car Spring attend un objet
    if (this.modalPost) {
      formData.append("post", new Blob([JSON.stringify(this.modalPost)], { type: "application/json" }));
    }
    this.postService.CreatePost(formData).subscribe({
      next: (res) => {
        console.log(res);
      },
      error: (err) => {
        console.log(err);
      }
    })
    this.getAllPost();
    console.log('Post to create:', this.modalPost);
    this.closeModal('post');
  }

  search() {
    this.matchService.search(this.match).subscribe({
      next: (res) => {
        console.log(res);
      },
      error: (err) => {
        console.log(err);
        
      }
    })
    this.getMatchs();
    console.log('Post to create:', this.modalPost);
    this.closeModal('search');
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement; // cast vers HTMLInputElement
    if (!input.files || input.files.length === 0) return;
    this.selectedFile = input.files[0];
  }


  // Ajouter un input
  addWord() {
    if (this.modalPost.description[this.modalPost.description.length - 1] == '') {
      return
    }
    this.modalPost.description.push('');
  }

  // Supprimer un input
  removeWord(index: number) {
    if (this.modalPost.description.length == 1) {
      return
    }
    this.modalPost.description.splice(index, 1);
  }
  // Dans ton component
  trackByIndex(index: number, item: any) {
    return index; // ou un id unique si tu en as un
  }



  user: UserResponse = { userId: null, email: null, name: null, role: null };
  post: PostResponse[] = new Array();
  matchs: MatchReponse[] = new Array();
  match: MatchReponse = {id : "", userId: "", keyword: "", type: "", lieu: "", date: "", description: [], category: "", matchedPostIds: [], active: "", createdAt: "", lastMatchDate: "", etat : new Array() };
  error: string | null = "";
  nombre_annonce: string = "";
  nombre_match: string = "";

  getUser() {
    //recuperer le profile de l'utilisateur
    this.dashboardservice.getUser().subscribe({
      next: (res: UserResponse) => {
        this.user = res;
      },
      error: (err) => {
        this.error = "undefined";
      }
    })
  }

  getMatchs() {
    this.dashboardservice.getMatchs().subscribe({
      next: (res: MatchReponse[]) => {
        console.log("my match : ", res);
        this.matchs = res;
      },
      error: (err) => {
        console.log("error ", err);
        this.error = "undefined";
      }
    })
  }

  getAllPost() {
    this.dashboardservice.getAllPostById().subscribe({
      next: (res: PostResponse[]) => {
        console.log("my posts : ", res);
        this.post = res;
      },
      error: (err) => {
        console.log("error ", err);
        this.error = "undefined";
      }
    })
  }

  ngOnInit() {
    this.getUser();
    this.getAllPost();
    this.getMatchs();
    //recuperer les post de l'utilisateur
  }

}

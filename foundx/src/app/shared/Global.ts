import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root', // Angular crée une instance globale accessible partout
})
export class Global {
    categories: string[] = [
        'ELECTRONIQUE',    // Appareils électroniques : téléphones, ordinateurs, tablettes
        'VETEMENTS',       // Vêtements, chaussures, accessoires de mode
        'MAISON',          // Meubles, décoration, électroménager
        'CUISINE',         // Ustensiles, appareils de cuisine, vaisselle
        'SPORTS',          // Équipements et accessoires sportifs
        'JOUETS',          // Jouets, jeux pour enfants
        'LIVRES',          // Livres, magazines
        'ANIMAUX',         // Produits pour animaux, animaux eux-mêmes
        'VEHICULES',       // Véhicules, pièces détachées
        'DIVERS'
    ];
}

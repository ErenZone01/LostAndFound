package com.service_matching.match.models;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Match {
    private String id;                        // ID du match/recherche
    private String userId;                    // Propriétaire de la recherche

    // Critères de recherche
    private String keyword;                   // Mot-clé de recherche
    private String type;                      // Type d'objet recherché
    private String lieu;                      // Lieu où chercher
    private String date;                      // Date ou période de recherche
    private List<String> description;         // Mots-clés ou détails
    private Category category;                // Catégorie de l'objet

    // Résultats trouvés
    private List<String> matchedPostIds;      // Liste des IDs d'annonces trouvées


    // Suivi
    private Boolean active;                   // Recherche encore active ou non
    private LocalDateTime createdAt;          // Date de création
    private LocalDateTime lastMatchDate;      // Dernière fois qu'un match a été trouvé
    private List<State> etat;
}

package com.service_matching.match.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.service_matching.match.Repositories.PostRepositories;
import com.service_matching.match.models.Category;
import com.service_matching.match.models.Post;

@Service
public class Filtre {

    public List<Post> filtre(List<Post> allPosts, Category category, String date, String lieu) {

        Set<Post> result = new HashSet<>();

        // 🔹 Filtrer par catégorie
        if (category != null) {
            result.addAll(allPosts.stream()
                    .filter(post -> post.getCategory().toString().equalsIgnoreCase(category.toString()))
                    .toList());
        }

        // 🔹 Filtrer par lieu
        if (lieu != null) {
            result.addAll(allPosts.stream()
                    .filter(post -> post.getLieu() != null && post.getLieu().equalsIgnoreCase(lieu))
                    .toList());
        }

        // 🔹 Filtrer par date
        if (date != null) {
            result.addAll(allPosts.stream()
                    .filter(post -> post.getDate() != null && post.getDate().equalsIgnoreCase(date))
                    .toList());
        }

        // Si aucun filtre n'a été appliqué, renvoyer tout
        if (category == null && lieu == null && date == null) {
            return allPosts;
        }

        if (result.isEmpty()) {
            return allPosts; // Si aucun résultat, renvoyer tous les posts
        }

        return new ArrayList<>(result);
    }
}

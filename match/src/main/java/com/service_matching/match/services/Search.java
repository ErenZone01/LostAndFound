package com.service_matching.match.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service_matching.match.Repositories.MatchRepositories;
import com.service_matching.match.Repositories.PostRepositories;
import com.service_matching.match.models.Category;
import com.service_matching.match.models.Match;
import com.service_matching.match.models.MatchEventDTO;
import com.service_matching.match.models.Post;
import com.service_matching.match.models.State;

@Service
public class Search {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private KafkaProducerService kafkaProducer;

    @Autowired
    private PostRepositories postRepositories;

    @Autowired
    private MatchRepositories matchRepositories;

    private static final JaroWinklerSimilarity jw = new JaroWinklerSimilarity();

    private void sendNotifications(String userId, List<String> postIds) {
        postIds.forEach(postId -> {
            Post post = postRepositories.findByPostId(postId);
            if (post != null) {
                try {
                    MatchEventDTO event = new MatchEventDTO(post.getUserId(), userId, post.getPostId());
                    String jsonEvent = new ObjectMapper().writeValueAsString(event);
                    kafkaProducer.sendEventDTO(jsonEvent);
                    System.out.println("📨 Notification envoyée : " + jsonEvent);
                } catch (JsonProcessingException e) {
                    System.err.println("❌ Erreur envoi notif : " + e.getMessage());
                }
            }
        });

    }

    public Match search(String keyword, Category category, String date,
            String lieu, String userId) {
        List<Post> objects = postRepositories.findAll();
        int threshold = 50; // pertinence minimale
        List<Post> result = objects.stream()
                .filter(p -> calculateScore(p, keyword, category, date, lieu, userId) >= threshold)
                .sorted((p1, p2) -> calculateScore(p2, keyword, category, date, lieu, userId)
                        - calculateScore(p1, keyword, category, date, lieu, userId)) // tri par score
                .collect(Collectors.toList());

        // Vérifier si un match existe déjà avec ces critères
        Optional<Match> existingMatchOpt = matchRepositories.findByKeywordAndCategoryAndDateAndLieuAndUserId(
                keyword, category.toString(), date, lieu, userId);

        Match match;
        if (existingMatchOpt.isPresent()) {
            // ✅ Déjà existant → on met à jour
            match = existingMatchOpt.get();
            List<String> postId = result.stream()
                    .map(Post::getPostId)
                    .toList();
            List<State> existedState = match.getEtat();
            postId.forEach(s -> {
                if (!match.getMatchedPostIds().contains(s)){
                    State newState = new State();
                    newState.setPostId(s);
                    newState.setState(false);
                    existedState.add(newState);
                }
            });
            match.setEtat(existedState);
            match.setMatchedPostIds(postId);
            match.setLastMatchDate(LocalDateTime.now()); // exemple de champ update
        } else {
            // 🆕 Nouveau match
            match = new Match();
            match.setKeyword(keyword);
            match.setCategory(category);
            match.setDate(date);
            match.setLieu(lieu);
            match.setUserId(userId);
            match.setActive(false);
            // match.setEtat(new ArrayList<>());

            List<String> postId = result.stream()
                    .map(Post::getPostId)
                    .toList();

            List<State> existedState = new ArrayList<>();
            postId.forEach(s -> {
                State newState = new State();
                newState.setPostId(s);
                newState.setState(false);
                existedState.add(newState);
            });
            match.setEtat(existedState);
            match.setMatchedPostIds(postId);

            match.setCreatedAt(LocalDateTime.now());
        }

        matchRepositories.save(match);

        return match;
    }

    public int calculateScore(Post p, String keyword, Category category, String date,
            String lieu, String userId) {
        int score = 0;

        // 1. Catégorie
        if (category != null) {
            if (p.getCategory().toString().equalsIgnoreCase(category.toString())) {
                score += 50;
            } else if (similarity(p.getCategory().toString(), category.toString()) > 0.7) {
                score += 25; // match approximatif
            }
        }

        // 2. Type
        if (keyword != null && !keyword.isEmpty()) {
            if (p.getType().equalsIgnoreCase(keyword)) {
                score += 20;
            } else if (similarity(p.getType(), keyword) > 0.7) {
                score += 10;
            }
        }

        // 3. Description
        if (p.getDescription() != null && keyword != null) {
            if (p.getDescription().contains(keyword)) {
                score += 15;
            } else if (similarity2(p.getDescription(), keyword) > 0.5) {
                score += 7;
            }
        }

        // 4. Lieu
        if (p.getLieu() != null && lieu != null) {
            if (p.getLieu().toLowerCase().contains(lieu.toLowerCase())) {
                score += 10;
            }
        }

        // 5. Date
        if (p.getDate() != null && date != null) {
            if (p.getDate().toLowerCase().contains(date.toLowerCase())) {
                score += 5;
            }
        }

        return score;
    }

    /**
     * Retourne un score entre 0 et 1 représentant la similarité entre deux chaînes.
     */
    public static double similarity(String s1, String s2) {
        if (s1 == null || s2 == null)
            return 0.0;
        return jw.apply(s1.toLowerCase(), s2.toLowerCase());
    }

    // Score pour un tableau de chaînes vs une chaîne
    public static double similarity2(List<String> array, String keyword) {
        if (array == null || keyword == null)
            return 0.0;
        double maxScore = 0.0;
        for (String s : array) {
            double score = similarity(s, keyword);
            if (score > maxScore)
                maxScore = score;
        }
        return maxScore; // ou retourner la moyenne si tu préfères
    }
}

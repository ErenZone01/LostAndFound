package com.service_matching.match.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
        int threshold = 50;
        List<Post> result = objects.stream()
                .filter(p -> calculateScore(p, keyword, category, date, lieu, userId) >= threshold)
                .sorted((p1, p2) -> calculateScore(p2, keyword, category, date, lieu, userId)
                        - calculateScore(p1, keyword, category, date, lieu, userId))
                .collect(Collectors.toList());

        List<String> currentPostIds = result.stream()
                .map(Post::getPostId)
                .toList();

        Optional<Match> existingMatchOpt = matchRepositories.findByKeywordAndCategoryAndDateAndLieuAndUserId(
                keyword, category.toString(), date, lieu, userId);

        Match match;
        List<String> newPostIds = new ArrayList<>();

        if (existingMatchOpt.isPresent()) {
            match = existingMatchOpt.get();
            List<State> existedState = match.getEtat();

            // Détecter les nouveaux postIds
            newPostIds = currentPostIds.stream()
                    .filter(p -> !match.getMatchedPostIds().contains(p))
                    .toList();

            // Ajouter les nouveaux dans état
            newPostIds.forEach(s -> {
                State newState = new State();
                newState.setPostId(s);
                newState.setState(false);
                existedState.add(newState);
            });

            match.setEtat(existedState);
            match.setMatchedPostIds(currentPostIds);
            match.setLastMatchDate(LocalDateTime.now());

        } else {
            match = new Match();
            match.setKeyword(keyword);
            match.setCategory(category);
            match.setDate(date);
            match.setLieu(lieu);
            match.setUserId(userId);
            match.setActive(false);

            List<State> states = new ArrayList<>();
            currentPostIds.forEach(s -> {
                State newState = new State();
                newState.setPostId(s);
                newState.setState(false);
                states.add(newState);
            });
            match.setEtat(states);
            match.setMatchedPostIds(currentPostIds);
            match.setCreatedAt(LocalDateTime.now());

            newPostIds = currentPostIds; // premier match → tout est nouveau
        }

        // ✅ Envoi de notif uniquement pour les nouveaux
        if (!newPostIds.isEmpty()) {
            sendNotifications(userId, newPostIds);
        }

        matchRepositories.save(match);
        return match;
    }

    public int calculateScore(Post p, String keyword, Category category, String date,
            String lieu, String userId) {
        int score = 0;

        if (category != null) {
            if (p.getCategory().toString().equalsIgnoreCase(category.toString())) {
                score += 50;
            } else if (similarity(p.getCategory().toString(), category.toString()) > 0.7) {
                score += 25;
            }
        }

        if (keyword != null && !keyword.isEmpty()) {
            if (p.getType().equalsIgnoreCase(keyword)) {
                score += 20;
            } else if (similarity(p.getType(), keyword) > 0.7) {
                score += 10;
            }
        }

        if (p.getDescription() != null && keyword != null) {
            if (p.getDescription().contains(keyword)) {
                score += 15;
            } else if (similarity2(p.getDescription(), keyword) > 0.5) {
                score += 7;
            }
        }

        if (p.getLieu() != null && lieu != null) {
            if (p.getLieu().toLowerCase().contains(lieu.toLowerCase())) {
                score += 10;
            }
        }

        if (p.getDate() != null && date != null) {
            if (p.getDate().toLowerCase().contains(date.toLowerCase())) {
                score += 5;
            }
        }

        return score;
    }

    public static double similarity(String s1, String s2) {
        if (s1 == null || s2 == null)
            return 0.0;
        return jw.apply(s1.toLowerCase(), s2.toLowerCase());
    }

    public static double similarity2(List<String> array, String keyword) {
        if (array == null || keyword == null)
            return 0.0;
        double maxScore = 0.0;
        for (String s : array) {
            double score = similarity(s, keyword);
            if (score > maxScore)
                maxScore = score;
        }
        return maxScore;
    }
}

package com.service_matching.match.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.service_matching.match.Repositories.MatchRepositories;
import com.service_matching.match.Repositories.PostRepositories;
import com.service_matching.match.models.Match;
import com.service_matching.match.models.Post;
import com.service_matching.match.models.State;

@Service
public class MatchService {

    @Autowired
    private PostRepositories postRepositories;

    @Autowired
    private MatchRepositories matchRepositories;

    @Autowired
    private Search search;

    public List<Match> getMatchByUserId(String userId) {
        List<Match> matchs = matchRepositories.findByUserId(userId);
        if (matchs == null) {
            return new ArrayList<>();
        }
        return matchs;
    }

    public List<Post> getPostByMatchId(String matchId) {
        Match match = matchRepositories.findById(matchId).get();
        if (match == null) {
            return new ArrayList<>();
        }
        List<Post> posts = new ArrayList<>();
        match.getMatchedPostIds().forEach((m) -> {
            posts.add(postRepositories.findByPostId(m));
        });

        return posts;
    }

    public Match getMatchById(String matchId) {
        if (!matchRepositories.findById(matchId).isPresent()) {
            return new Match();
        }
        return matchRepositories.findById(matchId).get();
    }

    // 41ed742d-650c-4536-b8b7-fb7fd35b3555
    // 41ed742d-650c-4536-b8b7-fb7fd35b3555

    public String changeState(String matchId, State state) {
        if (!matchRepositories.findById(matchId).isPresent()) {
            return "Update error";
        }
        Match match = matchRepositories.findById(matchId).get();
        List<State> existedState = match.getEtat();
        existedState.forEach((s) -> {
            System.out.println("tout les state existant : " + existedState);
            if (state.getPostId().equals(s.getPostId())) {
                System.out.println("l'update se passe ici et le stte est à : " + state.isState());
                s.setState(state.isState());
            }
        });
        match.setEtat(existedState);

        // match.setEtat(match.getEtat());
        matchRepositories.save(match);
        return "Update successfull";
    }

    public String changeActive(String matchId, String postId) {
        if (!matchRepositories.findById(matchId).isPresent() || postRepositories.findByPostId(postId) == null) {
            return "Update error";
        }
        Match match = matchRepositories.findById(matchId).get();
        Post post = postRepositories.findByPostId(postId);
        match.setActive(true);
        post.setActive(false);
        matchRepositories.save(match);
        postRepositories.save(post);
        return "Update successfull";
    }

    public List<Match> getMatchsFromIdPost(String idPost) {
        List<Match> existeMatch = matchRepositories.findAll();
        List<Match> newMatch = new ArrayList<>();
        existeMatch.forEach(m -> {
            if (m.getMatchedPostIds().contains(idPost)) {
                newMatch.add(m);
            }
        });
        System.out.println("match found : " + newMatch);
        return newMatch;
    }

    public void UpdateMatchWhenPostCreated(Post post) {
        List<Match> matchs = matchRepositories.findByActiveFalse();
        if (matchs == null) {
            return;
        }
        matchs.forEach(m -> {
            search.search(m.getKeyword(), m.getCategory(), m.getDate(), m.getLieu(), post.getUserId());
        });
    }

}

package com.service_matching.match.controllers;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.service_matching.match.models.Category;
import com.service_matching.match.models.Match;
import com.service_matching.match.models.Post;
import com.service_matching.match.models.State;
import com.service_matching.match.services.MatchService;
import com.service_matching.match.services.Search;





@RestController
@RequestMapping("/api")
public class SearchAndFilterController {
    // http://localhost:8083/api/sort?category=electronics&lieu=Paris&date=2023-01-01
    // This controller can be used to handle requests related to filtering posts
    // You can define endpoints here that utilize the Filtre service for filtering
    // logic
    private final Search searchService;
    private final MatchService matchService;

    public SearchAndFilterController(Search searchService, MatchService matchService) {
        this.searchService = searchService;
        this.matchService = matchService;
    }

    @GetMapping("/getMatchs")
    public List<Match> getMatchByUserId(@RequestHeader(value = "X-User-Id", required = true) String userId){
         return matchService.getMatchByUserId(userId);
    }

    
    @GetMapping("/getMatchById")
    public Match getMatchById(@RequestParam(required=true) String matchId){
         return matchService.getMatchById(matchId);
    }

    @GetMapping("/getPostByMatchId")
    public List<Post> getPostByMatchId(@RequestParam(required=true) String matchId){
         return matchService.getPostByMatchId(matchId);
    }

    @PostMapping("/getMatchsFromIdPost")
    public List<Match> getMatchsFromIdPost(@RequestBody(required = true) String idPost){
        return matchService.getMatchsFromIdPost(idPost);
    }

    @PutMapping("updateMatchState")
    public String updateState(@RequestParam(required = true) String matchId, @RequestParam(required = true) String postId, @RequestParam(required = true) boolean state){
        State tmp = new State();
        tmp.setPostId(postId);
        tmp.setState(state);
        System.out.println("tmp : "+ tmp);
        return matchService.changeState(matchId, tmp);
    }

    @PutMapping("updateMatchAndPostActive")
    public String updateAcitve(@RequestParam(required = true) String matchId, @RequestParam(required = true) String postId){
        return matchService.changeActive(matchId, postId);
    }

    // Example endpoint:
    @PostMapping("/sort")
    public void filterPosts(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category, 
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String lieu,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
         System.out.println("User ID: " + userId);
        // System.out.println("Role   : " + role);
        System.out.println(searchService.search(keyword, category, date, lieu, userId));
    }

}

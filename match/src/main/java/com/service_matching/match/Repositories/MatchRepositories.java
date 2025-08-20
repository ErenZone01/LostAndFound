package com.service_matching.match.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.service_matching.match.models.Category;
import com.service_matching.match.models.Match;

@Repository
public interface MatchRepositories extends MongoRepository<Match, String> {

        Match findByUserIdAndKeywordAndCategoryAndLieuAndDate(String userId, String keyword, Category category,
                        String lieu,
                        String date);

        List<Match> findByUserId(String userId);

        Optional<Match> findById(String matchId);

        List<Match> findByMatchedPostIds(String matchedPostIds);

        List<Match> findByActiveFalse();

        Optional<Match> findByKeywordAndCategoryAndDateAndLieuAndUserId(
                        String keyword, String category, String date, String lieu, String userId);

}

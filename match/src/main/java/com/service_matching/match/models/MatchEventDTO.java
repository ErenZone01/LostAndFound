package com.service_matching.match.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Data
public class MatchEventDTO {
    private String ownerId;
    private String senderId;
    private String postId;
}

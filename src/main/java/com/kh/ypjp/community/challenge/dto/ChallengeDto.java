package com.kh.ypjp.community.challenge.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChallengeDto {
    private Long challengeNo;
    private Long userNo;
    private Long chInfoNo;
    private String videoUrl;
    private int views;
    private LocalDateTime createdAt;
    private String username;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int likes;
    private String postImageUrl;
    private Integer imageNo;
}
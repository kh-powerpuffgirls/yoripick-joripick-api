package com.kh.ypjp.community.challenge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
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
    private String sik_bti;

    // FreeDto와 동일하게 필드명 변경
    private String originName;
    private String serverName;
    private String imageUrl;
    private Integer imageNo;
}

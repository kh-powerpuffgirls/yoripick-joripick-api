package com.kh.ypjp.community.challenge.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChallengeDto {
    private Long challengeNo; // CHALLENGE.challenge_no
    private Long userNo; // CHALLENGE.user_no
    private Long chInfoNo; // CHALLENGE.ch_info_no
    private String videoUrl; // CHALLENGE.video_url
    private int views; // CHALLENGE.views
    private LocalDateTime createdAt; // CHALLENGE.created_at

    private String username; // USERS.username
    
    private String title; // CHALLENGE_INFO.title
    private String imageUrl; // CHALLENGE_INFO.image_url (챌린지 정보 이미지)
    private LocalDateTime startDate; // CHALLENGE_INFO.start_date
    private LocalDateTime endDate; // CHALLENGE_INFO.end_date
    private int likes; // CHALLENGE_INFO.likes
    
    // 이 필드는 사용자가 등록한 이미지입니다. (CHALLENGE.image_no를 통해 매핑)
    private String postImageUrl;
    
    // 이 필드는 게시글을 수정/등록할 때 사용자가 직접 올린 이미지 파일명을 저장하기 위한 용도입니다.
    private int imageNo;
}
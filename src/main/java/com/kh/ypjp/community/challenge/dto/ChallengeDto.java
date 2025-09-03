package com.kh.ypjp.community.challenge.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChallengeDto {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String author;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private int views;
    private int likes;
}
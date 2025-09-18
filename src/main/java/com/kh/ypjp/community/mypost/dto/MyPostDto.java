package com.kh.ypjp.community.mypost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MyPostDto {
    private Integer id;               // 게시글 번호
    private String title;             // 제목
    private String description;       // 내용
    private LocalDateTime createdDate;// 작성일
    private int views;                // 조회수
    private Integer userId;           // 작성자 번호
    private String category;          // 게시판 종류: BOARD, RECIPE, CHALLENGE, MARKET
}

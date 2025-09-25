package com.kh.ypjp.community.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketSellDto {

    // --- DB 컬럼 (판매글 정보) ---
    private Long productId;
    private Long userNo;        // ✨ (프론트엔드 authorNo 역할) 작성자 유저 번호
    private String title;
    private Integer imageNo;
    private String name;
    private String detail;
    private Long price;
    private int quantity;
    private Date createdAt;
    private Date deadline;
    private String phone;
    private String accountNo;
    private int views;
    private String deleteStatus;
    private boolean alwaysOnSale;
    
    // --- JOIN/가공 필드 (프론트엔드 전달용) ---
    private String author;              // 작성자 닉네임
    private String authorProfileUrl;    // ✨ (수정된 경로) 프로필 이미지 URL (e.g., /images/profile/{userNo}/파일명)
    private String sikBti;              // 식BTI 정보
    private String imageUrl;            // 게시글 대표 이미지 URL (market/{userNo}/파일명)
    private String serverName;          // 서버에 저장된 게시글 이미지 경로 (DB 저장용)
    private String originName;          // 게시글 이미지 원본 이름 (DB 저장용)
    
    // --- 구매/폼 관련 필드 ---
    private Long formNo;
    private List<MarketBuyDto> buyForms;
}
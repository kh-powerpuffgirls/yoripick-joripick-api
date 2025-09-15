package com.kh.ypjp.community.recipe.dto;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReviewDto {
	// 리뷰 작성을 위해 프론트에서 보낼 데이터
    @Data
    public static class ReviewWriteRequest {
        private int rcpNo;
        private double stars;
        private String content;
        private MultipartFile image;
    }
    
    // 리뷰 목록 조회를 위해 프론트로 보낼 데이터
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewResponse {
        private int reviewNo;
        private double stars;
        private String content;
        private Date reviewDate;
        private String serverName; // 리뷰 이미지 경로
        private UserInfo userInfo; // 작성자 정보
    }
    
    // ReviewResponse 안에 포함될 작성자 정보
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private int userNo;
        private String username;
        private String sikBti;
        private String profileImage;
    }
    
    // 페이지네이션된 리뷰 목록을 감싸는 객체
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewPage {
        private List<ReviewResponse> reviews;
        private int totalPages;
    }
}

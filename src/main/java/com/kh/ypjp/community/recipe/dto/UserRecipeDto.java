package com.kh.ypjp.community.recipe.dto;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserRecipeDto {
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserRecipeResponse{
		private long rcpNo;		//레시피 번호
		private long userNo;		//유저 번호
		private String username;
		private String rcpName;	//레시피명
		private int categoryNo;	//카테고리 번호
		private String serverName;		//이미지 번호
		private int views;			//조회수
		private Date createdAt;	//작성일자
		private String userProfileImage;	// 작성자 프로필 이미지
		private String sikBti; 
	    private Double avgStars;         // ✨ 평균 별점
	    private int reviewCount;  	//리뷰개수
		
	}
	
	@Data
	@NoArgsConstructor
	public static class RecipeWriteRequest {
		private String rcpName;
        private String rcpInfo;
        private String tag;
        private int rcpMthNo;
        private int rcpStaNo;
        private String ingredients; // 재료 목록 (JSON 문자열)
        private MultipartFile mainImage;
        private List<String> stepDescriptions;
        private List<MultipartFile> stepImages;
    }

    @Data
    public static class IngredientInfo { // 재료 검색 결과 DTO
        private int ingNo;
        private String ingName;
        private double energy;
        private double carb;
        private double protein;
        private double fat;
        private double sodium;
    }
    
    @Data
    public static class IngredientJsonDto {
        private int ingNo; // JSON 필드명과 일치
        private String quantity;
        private int weight;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipePage {
        private List<UserRecipeResponse> recipes; // 현재 페이지의 레시피 목록
        private int totalPages;                   // 전체 페이지 수
        private long totalElements;               // 전체 게시글 수
    }
    

}

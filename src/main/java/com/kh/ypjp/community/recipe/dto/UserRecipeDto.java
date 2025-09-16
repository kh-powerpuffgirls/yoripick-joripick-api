package com.kh.ypjp.community.recipe.dto;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kh.ypjp.common.model.vo.Nutrient;
import com.kh.ypjp.community.recipe.model.vo.CookingStep;
import com.kh.ypjp.community.recipe.model.vo.RcpIngredient;

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
		private String sikBti; 
		private String userProfileImage;	// 작성자 프로필 이미지
		private String rcpName;	//레시피명
		private int categoryNo;	//카테고리 번호
		private String serverName;		//이미지 번호
		private int views;			//조회수
		private Date createdAt;	//작성일자
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
    
    @Data
	@NoArgsConstructor
	public static class RecipeDetailResponse {
    	private int rcpNo;
        private String rcpName;
        private String rcpInfo;
        private Date createdAt;
        private int views;
        private String tag;
        private boolean isOfficial;
        
        private String myLikeStatus; 

        // JOIN된 정보
        private String rcpMethod;
        private String rcpSituation;
        private String mainImage;

        // 집계 정보
        private int likeCount;
        private int reviewCount;
        private double avgStars;

        // 작성자 정보 객체 (별도 클래스로 분리)
        private Writer writer;

        // 영양 정보 객체
        private Nutrient totalNutrient;

        // 목록 정보 (서비스단에서 별도 조회 후 채워넣을 필드)
        private List<RcpIngredient> ingredients;
        private List<CookingStep> steps;

        // 작성자 정보를 담을 내부 클래스
        @Data
        public static class Writer {
            private long userNo;
            private String username;
            private String sikBti;
            private String profileImage;
        }
    }
    
    
    @Data
    @AllArgsConstructor
    public static class LikeResponse {
        private int likeCount;
        private int dislikeCount;
        private boolean isLiked;	//좋아요 상태
    }
}

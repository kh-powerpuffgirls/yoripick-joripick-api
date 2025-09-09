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
		private long rcp_no;		//레시피 번호
		private long user_no;		//유저 번호
		private String rcp_name;	//레시피명
		private int category_no;	//카테고리 번호
		private int image_no;		//이미지 번호
		private int views;			//조회수
		private Date created_at;	//작성일자
		
	}
	
	 @Data
	    public static class RecipeWriteRequest {
	        // 기본 정보
	        private String rcpName;         // 레시피 제목 (JSON 필드명: rcp_name)
	        private String rcpInfo;         // 레시피 소개 (JSON 필드명: rcp_info)
	        private String tag;             // 태그
	        private int rcpMthNo;           // 요리 방법 번호
	        private int rcpStaNo;           // 요리 종류 번호

	        // 재료 및 영양성분 정보
	        // 프론트에서 재료 객체 배열을 JSON 문자열로 변환하여 전송
	        private String ingredients;     // JSON 형태의 재료 목록 문자열
	        
	        // 대표 이미지
	        private MultipartFile mainImage;

	        // 요리 순서
	        // 각 순서의 설명과 이미지를 별도 리스트로 받음
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

}

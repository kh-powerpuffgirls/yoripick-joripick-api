
package com.kh.ypjp.recipe.dto;

public class RecipeDto {
	public static class Recipe{
		private long rcp_no;		//레시피 번호
		private long user_no;		//유저 번호
		private String rcp_name;	//레시피명
		private String rcp_info;	//레시피 정보(레시피 소개)
		private int rcp_mth_no;		//요리 방법(요리방법)
		private int rcp_sta_no;		//요리 상황(요리종류)
		private String tag;			//태그
		private int category_no;	//카테고리 번호
		private int nutrient_no;	//영양성분 정보
		private String ingredient;	//재료정보
		private String approval;	//공식레시피 승인 유무
		private int image_no;		//이미지 번호
		private int views;			//조회수
	}

}

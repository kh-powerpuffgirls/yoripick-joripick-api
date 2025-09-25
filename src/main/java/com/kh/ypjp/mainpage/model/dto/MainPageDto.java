package com.kh.ypjp.mainpage.model.dto;

import java.util.List;

import com.kh.ypjp.common.PageInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MainPageDto {

	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MainResponse {
		List<RecipeResponse> recipe;
		List<RecipeResponse> pickRecipe;
		List<RecipeResponse> ingPedia;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RecipeResponse {
		private long ingNo;
		private String ingName;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PickRecipeResponse {
		private long ingNo;
		private String ingName;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IngPediaResponse {
		private long ingNo;
		private String ingName;
	}

}

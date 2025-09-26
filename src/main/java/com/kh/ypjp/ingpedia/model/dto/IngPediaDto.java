package com.kh.ypjp.ingpedia.model.dto;

import java.util.List;

import com.kh.ypjp.common.PageInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class IngPediaDto {

	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IngListResponse {
		private long ingNo;
		private String ingName;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PagedIngListResponse {
		private List<IngListResponse> ingList;
		private PageInfo pageInfo;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IngDetailResponse {
//		INGREDIENT
		private long userNo;
		private long ingNo;
		private String ingName;
		private long ingCode;
		private String ingCodeName;
		private long nutrientNo;
		private String imgUrl;
		
//		NUTRIENT
		private Double energy;
		private Double carb;
		private Double protein;
		private Double fat;
		private Double sodium;
		
//		METHOD
		private String buyingTip;
		private String usageTip;
		private String storageMethod;
		private String preparation;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IngPairResponse {
//		PAIR
		private long pairNo;
		private String pairName;
		private String pairState;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IngPediaResponse {
		private IngDetailResponse ingDetail;
		private List<IngPairResponse> pairList;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IngPediaPost {
		private IngDetailResponse ingDetail;
		private List<IngPairResponse> pairList;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IngPediaPut {
		private IngDetailResponse ingDetail;
		private List<IngPairResponse> pairList;
	}
	
//	Mainpage
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IngPediaMainResponse {
		private long ingNo;
		private String ingName;
		private String imgUrl;

		private String buyingTip;
		private String usageTip;
		private String storageMethod;
		private String preparation;
	}
}

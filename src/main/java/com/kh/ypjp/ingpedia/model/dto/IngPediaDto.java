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
	public static class IngPediaResponse {
//		INGREDIENT
		private long userNo;
		private long ingNo;
		private String ingName;
		private long ingCode;
		private String ingCodeName;
		
//		NUTRIENT
		private long energy;
		private long carb;
		private long protein;
		private long fat;
		private long sodium;
		
//		METHOD
		private long methodNo;
		private String buyingTip;
		private String usageTip;
		private String storageMethod;
		private String preparation;
		
//		PAIR
		private List<PairResponse> pairList;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PairResponse {
//		PAIR
		private long pairNo;
		private String pairState;
	}
//	
//	@Data
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class IngListResponse {
//	    private List<IngPediaDto.IngResponse> list;
//	    private PageInfo pageInfo;
//	}
//	
//	@Data
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class IngCodeResponse {
//		private long ingCode;
//		private String ingCodeName;
//	}

//		NUTRIENT -- 아직은 쓸지 모르겠음... 여유 생기면 추가
//		private long energy;
//		private long carb;
//		private long protein;
//		private long fat;
//		private long sodium;
	
//	@Data
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class MyIngPut {
//		private long userNo;
//		private long ingNo;
//		private Date createdAt;
//		private Date expDate;
//		private String quantity;
//	}
	
	
}

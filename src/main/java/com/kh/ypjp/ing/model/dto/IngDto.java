package com.kh.ypjp.ing.model.dto;

import java.util.List;

import com.kh.ypjp.common.PageInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class IngDto {

	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IngResponse {
		private long userNo;
		private long ingNo;
		private String ingName;
		private long ingCode;
		private String ingCodeName;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IngListResponse {
	    private List<IngDto.IngResponse> list;
	    private PageInfo pageInfo;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IngCodeResponse {
		private long ingCode;
		private String ingCodeName;
	}

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

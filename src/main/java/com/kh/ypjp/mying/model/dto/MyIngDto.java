package com.kh.ypjp.mying.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MyIngDto {
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MyIngResponse {
//		GROCERY
		private long userNo;
		private long ingNo;
		private Date createdAt;
		private Date expDate;
		private String quantity;		
		
//		INGREDIENT
		private String ingName;
		private long ingCode;
		
//		ING_CODE
		private String ingCodeName;
	}

//		NUTRIENT -- 아직은 쓸지 모르겠음... 여유 생기면 추가
//		private long energy;
//		private long carb;
//		private long protein;
//		private long fat;
//		private long sodium;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MyIngPut {
		private long userNo;
		private long ingNo;
		private Date createdAt;
		private Date expDate;
		private String quantity;
	}
	
}

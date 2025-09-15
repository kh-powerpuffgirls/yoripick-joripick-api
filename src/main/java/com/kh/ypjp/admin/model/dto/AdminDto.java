package com.kh.ypjp.admin.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AdminDto {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChallengeForm {
		private Long formNo;
		private Long userNo;
		private String chTitle;
		private String description;
		private String reference;
		private Date createdAt;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Recipe {
		private Long rcpNo;
		private Long userNo;
		private String title;
		private String info;
		private String type;
		private Long reportNo;
		private String detail;
		private String content;
		private String reportedAt;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Report {
		private Long reportNo;
		private Long userNo;
		private String category;
		private String detail;
		private Long refNo;
		private String content;
		private Date reportedAt;
	}
}

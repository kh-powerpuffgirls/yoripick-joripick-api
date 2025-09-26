package com.kh.ypjp.admin.model.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AdminDto {
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Ingredient {
		private Long ingNo;
		private String ingName;
		private String ingCodeName;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CSinfo {
		private Long roomNo;
		private Long userNo;
		private String username;
		private Date time;
		private String content;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChatInfo {
		private Long roomNo;
		private String className;
		private List<ChatMsg> messages;
		
		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		public static class ChatMsg {
			private Long messageNo;
			private Long userNo;
			private String username;
			private String content;
			private Date time;
			private String hidden;
			private Long imageNo;
		}
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ClassInfo {
		private Long roomNo;
		private String className;
		private String username;
		private Integer passcode;
		private Long numPpl;
		private String deleteStatus;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommInfo {
		private String category;
		private Long commNo;
		private String title;
		private String username;
		private String createdAt;
		private Long views;
		private Long likes;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RecipeInfo {
		private Long rcpNo;
		private String rcpName;
		private Long views;
		private String approval;
		private Long likes;
		private Long dislikes;
		private Long bookmarks;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Announcement {
		private Long ancmtNo;
		private String content;
		private String startDate;
		private String endDate;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Challenge {
		private Long chInfoNo;
		private String title;
		private String startDate;
		private String endDate;
		private Long imageNo;
		
		private String imageUrl;
	}

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
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReportTargetDto {
		private String category;
	    private Long targetNo;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserInfo {
		private Long userNo;
		private String userName;
		private String status;
		private int banDays;
		private int reportNo;
		private int officialRcp;
		private int chRequest;
	}
}

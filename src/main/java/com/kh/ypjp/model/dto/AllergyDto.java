package com.kh.ypjp.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AllergyDto {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class AllergyList {
		private int allergyNo;
		private String name;
		private Integer category;
		private List<AllergyList> children;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UserAllergies {
		private List<AllergyList> allergyTree;
		private List<Long> userAllergies;
	}
}

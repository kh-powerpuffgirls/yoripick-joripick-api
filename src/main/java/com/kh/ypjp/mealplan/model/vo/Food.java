package com.kh.ypjp.mealplan.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food {
	private long foodNo;
	private String foodName;
	private int energy;
	private int carb;
	private int protein;
	private int fat;
	private int sodium;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FoodCode {
		private int foodCode;
		private String foodCodeName;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MealPlan {
		private Date mealDate;
		private String mealId;
		private String mealType;
		private long refNo;
		private int quantity;
	}
}
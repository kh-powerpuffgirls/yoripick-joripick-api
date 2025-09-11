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
	public static class FoodExt extends Food {
		private long mealNo;
		private String mealId;
		private int quantity;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FoodCode {
		private long mealNo;
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
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Statistics {
		private int energy;
	    private int carbs;
	    private int protein;
	    private int fat;
	    private int sodium;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NewFoodWrapper {
	    private Date mealDate;
	    private String mealId;
	    private String mealType;
	    private NewFood item;
	    
		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		public static class NewFood {
			private String foodName;
			private int energy;
			private int carb;
			private int protein;
			private int fat;
			private int sodium;
			private int quantity;
			
			private Long nutrientNo;
			private Long foodNo;
		}
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Recipe {
		private Long rcpNo;
		private String rcpName;
		private int energy;
//		private int carb;
//		private int protein;
//		private int fat;
//		private int sodium;
		private Long nutrientNo;
	}
}
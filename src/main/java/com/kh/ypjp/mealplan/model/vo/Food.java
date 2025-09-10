package com.kh.ypjp.mealplan.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food {
	private long foodNo;
	private String foodName;
	private String repFoodName;
	private int foodCode;
	private long userNo;
	
	private String foodCodeName;
	
	private int energy;
	private int carb;
	private int protein;
	private int fat;
	private int sodium;
}

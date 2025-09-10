package com.kh.ypjp.mealplan.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.mealplan.model.service.MealplanService;
import com.kh.ypjp.mealplan.model.vo.Food;
import com.kh.ypjp.mealplan.model.vo.Food.FoodCode;
import com.kh.ypjp.mealplan.model.vo.Food.MealPlan;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mealplan")
public class MealplanController {
	
	private final MealplanService mealplanService;
	
	@GetMapping("/foods")
    public List<Food> searchFoods(@RequestParam Map<String, Object> param) {
        return mealplanService.searchFoods(param);
    }
	
	@GetMapping("/foodCodes")
    public List<FoodCode> searchFoodCodes() {
        return mealplanService.searchFoodCodes();
    }
	
	@PostMapping("/newMeal/{userNo}")
	public void insertMeal(@RequestBody MealPlan meal, @PathVariable Long userNo) {
		if (userNo == null) return;
		Map <String, Object> param = new HashMap<>();
		param.put("meal", meal);
		param.put("userNo", userNo);
		System.out.println(mealplanService.insertMeal(param));
	}
}

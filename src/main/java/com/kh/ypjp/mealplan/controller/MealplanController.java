package com.kh.ypjp.mealplan.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.kh.ypjp.mealplan.model.vo.Food.FoodExt;
import com.kh.ypjp.mealplan.model.vo.Food.MealPlan;
import com.kh.ypjp.mealplan.model.vo.Food.NewFoodWrapper;
import com.kh.ypjp.mealplan.model.vo.Food.NewFoodWrapper.NewFood;
import com.kh.ypjp.mealplan.model.vo.Food.Recipe;
import com.kh.ypjp.mealplan.model.vo.Food.Statistics;

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
	
	@PostMapping("/meals/{userNo}")
	public void insertMeal(@RequestBody MealPlan meal, @PathVariable Long userNo) {
		if (userNo == null) return;
		Map <String, Object> param = new HashMap<>();
		param.put("meal", meal);
		param.put("userNo", userNo);
		System.out.println(mealplanService.insertMeal(param));
	}
	
	@GetMapping("/meals/{userNo}")
	public Map<String, List<FoodExt>> getMealList(
			@RequestParam String date,
			@PathVariable Long userNo) {
		if (userNo == null) return null;
		Map <String, Object> param = new HashMap<>();
		param.put("date", date);
		param.put("userNo", userNo);
		return mealplanService.getMealList(param);
	}
	
	@GetMapping("/stats/{userNo}")
	public Map<String, Statistics> getMealStats(
			@RequestParam String from,
			@RequestParam String to,
			@RequestParam Long userNo) {
		if (userNo == null) return null;
		Map <String, Object> param = new HashMap<>();
		param.put("from", from);
		param.put("to", to);
		param.put("userNo", userNo);
		return mealplanService.getMealStats(param);
	}
	
	@DeleteMapping("/meals/{mealNo}")
	public void deleteMeals(@PathVariable Long mealNo) {
		mealplanService.deleteMeals(mealNo);
	}
	
	@GetMapping("/recents/{userNo}")
	public List<FoodExt> getRecents(@PathVariable Long userNo) {
		return mealplanService.getRecents(userNo);
	}
	
	@PostMapping("/foods/{userNo}")
	public void insertFood(@RequestBody NewFoodWrapper wrapper, @PathVariable Long userNo) {
		if (userNo == null) return;
		NewFood food = wrapper.getItem();
		System.out.println(food.toString());
		Map <String, Object> param = new HashMap<>();
		param.put("wrapper", wrapper);
		param.put("food", food);
		param.put("userNo", userNo);
		System.out.println(mealplanService.insertFood(param));
	}
	
	@GetMapping("/recipes/{userNo}")
	public List<Recipe> getMyRecipes(@PathVariable Long userNo) {
	    return mealplanService.getMyRecipes(userNo);
	}
}

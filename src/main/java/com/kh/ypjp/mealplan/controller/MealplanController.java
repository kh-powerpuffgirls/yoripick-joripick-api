package com.kh.ypjp.mealplan.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Food>> searchFoods(@RequestParam Map<String, Object> param) {
		List<Food> foodList = mealplanService.searchFoods(param);
		if (foodList != null) {
			return ResponseEntity.ok().body(foodList); // 200
		}
		return ResponseEntity.notFound().build(); // 404
    }
	
	@GetMapping("/foodCodes")
    public ResponseEntity<List<FoodCode>> searchFoodCodes() {
        List<FoodCode> foodCodes = mealplanService.searchFoodCodes();
		if (foodCodes != null) {
			return ResponseEntity.ok().body(foodCodes); // 200
		}
		return ResponseEntity.notFound().build(); // 404
    }
	
	@PostMapping("/meals/{userNo}")
	public ResponseEntity<Void> insertMeal(@RequestBody MealPlan meal, @PathVariable Long userNo) {
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401
		}
		Map <String, Object> param = new HashMap<>();
		param.put("meal", meal);
		param.put("userNo", userNo);
		if (mealplanService.insertMeal(param) > 0) {
			return ResponseEntity.ok().build(); // 201
		} else {
			return ResponseEntity.badRequest().build(); // 400
		}
	}
	
	@GetMapping("/meals/{userNo}")
	public ResponseEntity<Map<String, List<FoodExt>>> getMealList(
			@RequestParam String date,
			@PathVariable Long userNo) {
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401
		}
		Map <String, Object> param = new HashMap<>();
		param.put("date", date);
		param.put("userNo", userNo);
		Map<String, List<FoodExt>> mealList = mealplanService.getMealList(param);
		if (mealList != null) {
			return ResponseEntity.ok().body(mealList); // 200
		}
		return ResponseEntity.notFound().build(); // 404
	}
	
	@GetMapping("/stats/{userNo}")
	public ResponseEntity<Map<String, Statistics>> getMealStats(
			@RequestParam String from,
			@RequestParam String to,
			@RequestParam Long userNo) {
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401
		}
		Map <String, Object> param = new HashMap<>();
		param.put("from", from);
		param.put("to", to);
		param.put("userNo", userNo);
		Map<String, Statistics> mealStats = mealplanService.getMealStats(param);
		if (mealStats != null) {
			return ResponseEntity.ok().body(mealStats); // 200
		}
		return ResponseEntity.notFound().build(); // 404
	}
	
	@DeleteMapping("/meals/{mealNo}")
	public ResponseEntity<Void> deleteMeals(@PathVariable Long mealNo) {
		if (mealNo == null) {
			return ResponseEntity.badRequest().build(); // 400
		}
		if (mealplanService.deleteMeals(mealNo) > 0) {
			return ResponseEntity.ok().build(); // 201
		}
		return ResponseEntity.badRequest().build(); // 400
	}
	
	@GetMapping("/recents/{userNo}")
	public ResponseEntity<List<FoodExt>> getRecents(@PathVariable Long userNo) {
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401
		}
		List<FoodExt> recents = mealplanService.getRecents(userNo);
		if (recents != null) {
			return ResponseEntity.ok().body(recents); // 200
		}
		return ResponseEntity.notFound().build(); // 404
	}
	
	@PostMapping("/foods/{userNo}")
	public ResponseEntity<Void> insertFood(@RequestBody NewFoodWrapper wrapper, @PathVariable Long userNo) {
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401
		}
		NewFood food = wrapper.getItem();
		Map <String, Object> param = new HashMap<>();
		param.put("wrapper", wrapper);
		param.put("food", food);
		param.put("userNo", userNo);
		if (mealplanService.insertFood(param) > 0) {
			return ResponseEntity.ok().build(); // 201
		} else {
			return ResponseEntity.badRequest().build(); // 400
		}
	}
	
	@GetMapping("/recipes/{userNo}")
	public ResponseEntity<List<Recipe>> getMyRecipes(@PathVariable Long userNo) {
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401
		}
		List<Recipe> myRecipes = mealplanService.getMyRecipes(userNo);
		if (myRecipes != null) {
			return ResponseEntity.ok().body(myRecipes); // 200
		}
		return ResponseEntity.notFound().build(); // 404
	}
	
}

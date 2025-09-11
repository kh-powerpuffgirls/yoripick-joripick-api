package com.kh.ypjp.mealplan.model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.ypjp.mealplan.model.dao.MealplanDao;
import com.kh.ypjp.mealplan.model.vo.Food;
import com.kh.ypjp.mealplan.model.vo.Food.FoodCode;
import com.kh.ypjp.mealplan.model.vo.Food.FoodExt;
import com.kh.ypjp.mealplan.model.vo.Food.MealPlan;
import com.kh.ypjp.mealplan.model.vo.Food.NewFoodWrapper;
import com.kh.ypjp.mealplan.model.vo.Food.NewFoodWrapper.NewFood;
import com.kh.ypjp.mealplan.model.vo.Food.Recipe;
import com.kh.ypjp.mealplan.model.vo.Food.Statistics;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MealplanService {
	
	private final MealplanDao mealplanDao;

	public List<Food> searchFoods(Map <String, Object> param) {
		return mealplanDao.searchFood(param);
	}

	public List<FoodCode> searchFoodCodes() {
		return mealplanDao.searchFoodCodes();
	}

	public int insertMeal(Map<String, Object> param) {
		return mealplanDao.insertMeal(param);
	}

	public Map<String, List<FoodExt>> getMealList(Map<String, Object> param) {
		List<FoodExt> foodItems = mealplanDao.getMealList(param);
		Map<String, List<FoodExt>> result = new HashMap<>();
		for (FoodExt foodItem : foodItems) {
			result.computeIfAbsent(foodItem.getMealId(), k -> 
			new ArrayList<>()).add(foodItem);
		}
		return result;
	}

	public Map<String, Statistics> getMealStats(Map<String, Object> param) {
		List<Map<String,Object>> rawList = mealplanDao.getMealStats(param);
		Map<String, Statistics> result = new HashMap<>();
		for (Map<String, Object> row : rawList) {
			Statistics stats = new Statistics();
			stats.setEnergy(((Number)row.get("ENERGY")).intValue());
			stats.setCarbs(((Number)row.get("CARBS")).intValue());
			stats.setProtein(((Number)row.get("PROTEIN")).intValue());
			stats.setFat(((Number)row.get("FAT")).intValue());
			stats.setSodium(((Number)row.get("SODIUM")).intValue());
            result.put((String)row.get("MEALDATE"), stats);
        }
		return result;
	}

	public int deleteMeals(Long mealNo) {
		return mealplanDao.deleteMeals(mealNo);
	}

	public List<FoodExt> getRecents(Long userNo) {
		return mealplanDao.getRecents(userNo);
	}

	@Transactional
	public int insertFood(Map<String, Object> param) {
		if (mealplanDao.insertNutrient(param) > 0) {
			if (mealplanDao.insertFood(param) > 0) {
				NewFoodWrapper wrapper = (NewFoodWrapper) param.get("wrapper");
				NewFood food = (NewFood) param.get("food");
				MealPlan meal = new MealPlan();
				meal.setMealDate(wrapper.getMealDate());
				meal.setMealId(wrapper.getMealId());
				meal.setMealType(wrapper.getMealType());
				meal.setRefNo(food.getFoodNo());
				meal.setQuantity(food.getQuantity());
				Map <String, Object> newParam = new HashMap<>();
				newParam.put("meal", meal);
				newParam.put("userNo", param.get("userNo"));
				return mealplanDao.insertMeal(newParam);
			}
		}
		return 0;
	}

	public List<Recipe> getMyRecipes(Long userNo) {
		List<Recipe> rcpList = mealplanDao.getMyRecipes(userNo);
		for (Recipe rcp : rcpList) {
			String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/images/")
	                .path(rcp.getServerName())
	                .toUriString();
			rcp.setImgUrl(imageUrl);
		}
		return rcpList;
	}

}

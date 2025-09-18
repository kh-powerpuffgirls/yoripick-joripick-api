package com.kh.ypjp.mealplan.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.mealplan.model.vo.Food;
import com.kh.ypjp.mealplan.model.vo.Food.FoodCode;
import com.kh.ypjp.mealplan.model.vo.Food.FoodExt;
import com.kh.ypjp.mealplan.model.vo.Food.Recipe;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MealplanDao {

	private final SqlSession session;
	
	public List<Food> searchFood(Map <String, Object> param) {
		return session.selectList("mealplan.searchFood", param);
	}

	public List<FoodCode> searchFoodCodes() {
		return session.selectList("mealplan.searchFoodCodes");
	}

	public int insertMeal(Map<String, Object> param) {
		return session.insert("mealplan.insertMeal", param);
	}

	public List<FoodExt> getMealList(Map<String, Object> param) {
		return session.selectList("mealplan.getMealList", param);
	}

	public List<Map<String, Object>> getMealStats(Map<String, Object> param) {
		return session.selectList("mealplan.getMealStats", param);
	}

	public int deleteMeals(Long mealNo) {
		return session.delete("mealplan.deleteMeals", mealNo);
	}

	public List<FoodExt> getRecents(Long userNo) {
		return session.selectList("mealplan.getRecents", userNo);
	}

	public int insertNutrient(Map<String, Object> param) {
		return session.insert("mealplan.insertNutrient", param);
	}
	
	public int insertFood(Map<String, Object> param) {
		return session.insert("mealplan.insertFood", param);
	}

	public List<Recipe> getMyRecipes(Long userNo) {
		return session.selectList("mealplan.getMyRecipes", userNo);
	}

}

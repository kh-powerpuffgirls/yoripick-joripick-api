package com.kh.ypjp.mealplan.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.ypjp.mealplan.model.dao.MealplanDao;
import com.kh.ypjp.mealplan.model.vo.Food;
import com.kh.ypjp.mealplan.model.vo.Food.FoodCode;

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

}

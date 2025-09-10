package com.kh.ypjp.mealplan.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.mealplan.model.vo.Food;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MealplanDao {

	private final SqlSession session;
	
	public List<Food> searchFood(Map <String, Object> param) {
		return session.selectList("mealplan.searchFood", param);
	}

}

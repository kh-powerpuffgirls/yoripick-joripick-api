package com.kh.ypjp.mealplan.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.mealplan.model.service.MealplanService;
import com.kh.ypjp.mealplan.model.vo.Food;

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
}

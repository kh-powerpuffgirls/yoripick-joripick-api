package com.kh.ypjp.community.recipe.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto;
import com.kh.ypjp.community.recipe.service.UserRecipeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserRecipeController {
	private final UserRecipeService recipeService;
	
	@GetMapping("/community/recipe")
	@CrossOrigin(origins = "http://localhost:5173/", exposedHeaders = "Location")
	public ResponseEntity<List<UserRecipeDto.UserRecipeResponse>> selectList(
				@RequestParam HashMap<String,Object> param
			){
		List<UserRecipeDto.UserRecipeResponse> list = recipeService.selectRecipe(param);
		
		return ResponseEntity.ok(list);
		
	}
	
}

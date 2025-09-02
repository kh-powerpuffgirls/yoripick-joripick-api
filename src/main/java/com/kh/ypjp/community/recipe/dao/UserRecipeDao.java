package com.kh.ypjp.community.recipe.dao;

import java.util.HashMap;
import java.util.List;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;


public interface UserRecipeDao {

	
	List<UserRecipeResponse> selectRecipe(HashMap<String, Object> param);

}

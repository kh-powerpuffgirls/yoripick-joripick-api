package com.kh.ypjp.community.recipe.service;

import java.util.HashMap;
import java.util.List;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;

public interface UserRecipeService {

	List<UserRecipeResponse> selectRecipe(HashMap<String, Object> param);

}

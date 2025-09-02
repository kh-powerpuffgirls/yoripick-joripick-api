package com.kh.ypjp.community.recipe.dao;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;

@Repository
public interface UserRecipeDao {

	
	List<UserRecipeResponse> selectRecipe(HashMap<String, Object> param);

}

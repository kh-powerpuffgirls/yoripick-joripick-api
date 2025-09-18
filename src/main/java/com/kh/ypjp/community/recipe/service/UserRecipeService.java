package com.kh.ypjp.community.recipe.service;

import java.util.HashMap;
import java.util.List;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipePage;
import com.kh.ypjp.community.recipe.model.vo.RcpMethod;
import com.kh.ypjp.community.recipe.model.vo.RcpSituation;

public interface UserRecipeService {

	RecipePage selectRecipePage(HashMap<String, Object> params);
    
	List<UserRecipeDto.UserRecipeResponse> selectRankingRecipes();

    List<RcpMethod> selectRcpMethods();

    List<RcpSituation> selectRcpSituations();

    List<UserRecipeDto.IngredientInfo> searchIngredients(String keyword);

    void createRecipe(UserRecipeDto.RecipeWriteRequest request, long userNo) throws Exception;
}
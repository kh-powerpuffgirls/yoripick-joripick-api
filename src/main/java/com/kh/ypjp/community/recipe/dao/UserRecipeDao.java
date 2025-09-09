package com.kh.ypjp.community.recipe.dao;

import java.util.HashMap;
import java.util.List;

import com.kh.ypjp.common.model.vo.Image;
import com.kh.ypjp.common.model.vo.Nutrient;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.IngredientInfo;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;
import com.kh.ypjp.community.recipe.model.vo.RcpMethod;
import com.kh.ypjp.community.recipe.model.vo.RcpSituation;
import com.kh.ypjp.community.recipe.model.vo.Recipe;
import com.kh.ypjp.community.recipe.model.vo.RecipeStep;


public interface UserRecipeDao {

	
	List<UserRecipeResponse> selectRecipe(HashMap<String, Object> param);

	List<RcpMethod> selectRcpMethods();


	void insertImage(Image mainImageVo);

	void insertNutrient(Nutrient totalNutrient);

	void insertRecipeStep(RecipeStep recipe);

	void insertRecipe(Recipe step);

	List<RcpSituation> selectRcpSituations();

	List<IngredientInfo> searchIngredients(String keyword);


}

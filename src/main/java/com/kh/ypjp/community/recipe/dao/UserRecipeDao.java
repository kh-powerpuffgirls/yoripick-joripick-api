package com.kh.ypjp.community.recipe.dao;

import java.util.HashMap;
import java.util.List;

import com.kh.ypjp.common.model.vo.Image;
import com.kh.ypjp.common.model.vo.Nutrient;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.IngredientInfo;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;
import com.kh.ypjp.community.recipe.model.vo.RcpDetail;
import com.kh.ypjp.community.recipe.model.vo.RcpIngredient;
import com.kh.ypjp.community.recipe.model.vo.RcpMethod;
import com.kh.ypjp.community.recipe.model.vo.RcpSituation;
import com.kh.ypjp.community.recipe.model.vo.Recipe;
import com.kh.ypjp.community.recipe.model.vo.RecipeStep;


public interface UserRecipeDao {

	List<UserRecipeDto.UserRecipeResponse> selectRecipeList(HashMap<String, Object> params);

    long selectRecipeCount(HashMap<String, Object> params);

    List<UserRecipeDto.UserRecipeResponse> selectRankingRecipes();

    List<RcpMethod> selectRcpMethods();
    
    List<RcpSituation> selectRcpSituations();

    List<UserRecipeDto.IngredientInfo> searchIngredients(String keyword);
    
    Nutrient findNutrientsByIngNo(int ingNo);

    int insertImage(Image image);

    int insertRecipe(Recipe recipe);
    
    int insertRcpIngredient(RcpIngredient ingredient);
    
    int insertRcpDetail(RcpDetail detail);
    
    int insertNutrient(Nutrient nutrient);

}

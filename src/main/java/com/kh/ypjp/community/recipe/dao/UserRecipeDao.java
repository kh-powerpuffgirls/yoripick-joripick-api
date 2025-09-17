package com.kh.ypjp.community.recipe.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kh.ypjp.common.model.vo.Image;
import com.kh.ypjp.common.model.vo.Nutrient;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipeDetailResponse;
import com.kh.ypjp.community.recipe.model.vo.CookingStep;
import com.kh.ypjp.community.recipe.model.vo.RcpDetail;
import com.kh.ypjp.community.recipe.model.vo.RcpIngredient;
import com.kh.ypjp.community.recipe.model.vo.RcpMethod;
import com.kh.ypjp.community.recipe.model.vo.RcpSituation;
import com.kh.ypjp.community.recipe.model.vo.Recipe;
import com.kh.ypjp.community.recipe.model.vo.Review;


public interface UserRecipeDao {

	List<UserRecipeDto.UserRecipeResponse> selectRecipeList(HashMap<String, Object> params);

    long selectRecipeCount(HashMap<String, Object> params);

    List<UserRecipeDto.UserRecipeResponse> selectRankingRecipes();

    List<RcpMethod> selectRcpMethods();
    
    List<RcpSituation> selectRcpSituations();

    List<UserRecipeDto.IngredientInfo> searchIngredients(String keyword);
    
    Nutrient findNutrientsByIngNo(int ingNo);

    int insertImage(Image image);

    long getNextRcpNo(); 
    
    int insertRecipe(Recipe recipe);
    
    int insertRcpIngredient(RcpIngredient ingredient);
    
    int insertRcpDetail(RcpDetail detail);
    
    int insertNutrient(Nutrient nutrient);
    
    RecipeDetailResponse selectRecipeDetail(Map<String, Object> params);
    
    int increaseViewCount(int rcpNo);
    
    
    //좋아요
    int findLike(Map<String, Object> params);
    void insertLike(Map<String, Object> params);
    void deleteLike(Map<String, Object> params);
    int countLikes(int rcpNo);

	int mergeLikeStatus(Map<String, Object> params);

	List<RcpIngredient> selectIngredientsByRcpNo(int rcpNo);

	List<CookingStep> selectStepsByRcpNo(int rcpNo);

	String findLikeStatus(Map<String, Object> params);

	int insertReview(Review review);

}

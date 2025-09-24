package com.kh.ypjp.community.recipe.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.OfficialRecipePage;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipeDetailResponse;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipePage;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipeWriteRequest;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.ReviewPageResponse;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.ReviewResponseDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.ReviewWriteRequest;
import com.kh.ypjp.community.recipe.model.vo.RcpMethod;
import com.kh.ypjp.community.recipe.model.vo.RcpSituation;

public interface UserRecipeService {

	RecipePage selectRecipePage(HashMap<String, Object> params);
    
	List<UserRecipeDto.UserRecipeResponse> selectRankingRecipes();

    List<RcpMethod> selectRcpMethods();

    List<RcpSituation> selectRcpSituations();

    List<UserRecipeDto.IngredientInfo> searchIngredients(String keyword);

    void createRecipe(UserRecipeDto.RecipeWriteRequest request, long userNo) throws Exception;
    
    RecipeDetailResponse selectRecipeDetail(int rcpNo, Long userNo, boolean increaseViewCount);
    
    UserRecipeDto.LikeResponse toggleLike(int rcpNo, long userNo);

	void updateLikeStatus(int rcpNo, Long userNo, String status);

	void createReview(int rcpNo, long userNo, ReviewWriteRequest request);

	ReviewPageResponse selectReviewPage(int rcpNo, int page, String sort);

	List<ReviewResponseDto> selectPhotoReviewList(int rcpNo);

	void deleteReview(int reviewNo, long userNo);

	void updateRecipe(int rcpNo, long userNo, UserRecipeDto.RecipeWriteRequest request) throws Exception;

	void deleteRecipe(int rcpNo);

	RecipeDetailResponse selectOfficialRecipeDetail(int rcpNo,Long userNo, boolean increaseViewCount);

	UserRecipeDto.BookmarkResponse toggleBookmark(int rcpNo, long userNo);

	OfficialRecipePage selectOfficialRecipePage(HashMap<String, Object> params);

}
package com.kh.ypjp.community.recipe.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import org.springframework.stereotype.Repository;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRecipeDaoImpl implements UserRecipeDao{

	private final SqlSessionTemplate session;
    private static final String NAMESPACE = "userRecipeMapper.";

    public List<UserRecipeResponse> selectRecipeList(HashMap<String, Object> params) {
        return session.selectList("userRecipeMapper.selectRecipeList", params);
    }
    
    // ✨ 추가: 전체 게시글 수 조회
    public long selectRecipeCount(HashMap<String, Object> params) {
        return session.selectOne("userRecipeMapper.selectRecipeCount", params);
    }
    
    // ✨ 추가: 랭킹 목록 조회
    public List<UserRecipeResponse> selectRankingRecipes() {
        return session.selectList("userRecipeMapper.selectRankingRecipes");
    }

    @Override
    public List<RcpMethod> selectRcpMethods() {
        return session.selectList(NAMESPACE + "selectRcpMethods");
    }

    @Override
    public List<RcpSituation> selectRcpSituations() {
        return session.selectList(NAMESPACE + "selectRcpSituations");
    }

    @Override
    public List<UserRecipeDto.IngredientInfo> searchIngredients(String keyword) {
        return session.selectList(NAMESPACE + "searchIngredients", keyword);
    }

    @Override
    public int insertImage(Image image) {
        return session.insert(NAMESPACE + "insertImage", image);
    }

    @Override
    public int insertNutrient(Nutrient nutrient) { // ✨ 메소드 구현 추가
        return session.insert("userRecipeMapper.insertNutrient", nutrient);
    }
    
    @Override
    public int insertRecipe(Recipe recipe) {
        return session.insert(NAMESPACE + "insertRecipe", recipe);
    }

    @Override
    public int insertRcpIngredient(RcpIngredient ingredient) {
        return session.insert(NAMESPACE + "insertRcpIngredient", ingredient);
    }

    @Override
    public int insertRcpDetail(RcpDetail detail) {
        return session.insert(NAMESPACE + "insertRcpDetail", detail);
    }
	
    @Override
    public Nutrient findNutrientsByIngNo(int ingNo) { 
        return session.selectOne("userRecipeMapper.findNutrientsByIngNo", ingNo);
    }

}

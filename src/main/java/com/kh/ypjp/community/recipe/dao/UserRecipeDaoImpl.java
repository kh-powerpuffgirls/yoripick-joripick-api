package com.kh.ypjp.community.recipe.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.common.model.vo.Image;
import com.kh.ypjp.common.model.vo.Nutrient;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipeDetailResponse;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;
import com.kh.ypjp.community.recipe.model.vo.RcpDetail;
import com.kh.ypjp.community.recipe.model.vo.RcpIngredient;
import com.kh.ypjp.community.recipe.model.vo.RcpMethod;
import com.kh.ypjp.community.recipe.model.vo.RcpSituation;
import com.kh.ypjp.community.recipe.model.vo.Recipe;

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
    public int insertNutrient(Nutrient nutrient) { 
        return session.insert("userRecipeMapper.insertNutrient", nutrient);
    }
    
    @Override
    public long getNextRcpNo() { 
        return session.selectOne("userRecipeMapper.getNextRcpNo");
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
    
    // 상세 조회 메소드 구현
    @Override
    public RecipeDetailResponse selectRecipeDetail(int rcpNo) {
        return session.selectOne(NAMESPACE + "selectRecipeDetail", rcpNo);
    }

    // 조회수 증가 메소드 구현
    @Override
    public int increaseViewCount(int rcpNo) {
        return session.update(NAMESPACE + "increaseViewCount", rcpNo);
    }

    //좋아요 기능
    @Override
    public int findLike(Map<String, Object> params) {
        return session.selectOne(NAMESPACE + "findLike", params);
    }

    @Override
    public void insertLike(Map<String, Object> params) {
        session.insert(NAMESPACE + "insertLike", params);
    }

    @Override
    public void deleteLike(Map<String, Object> params) {
        session.delete(NAMESPACE + "deleteLike", params);
    }

    @Override
    public int countLikes(int rcpNo) {
        return session.selectOne(NAMESPACE + "countLikes", rcpNo);
    }
}

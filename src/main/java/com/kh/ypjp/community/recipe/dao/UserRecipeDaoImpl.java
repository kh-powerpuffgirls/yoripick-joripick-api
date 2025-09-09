package com.kh.ypjp.community.recipe.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import org.springframework.stereotype.Repository;

import com.kh.ypjp.common.model.vo.Image;
import com.kh.ypjp.common.model.vo.Nutrient;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.IngredientInfo;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;
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

    private static final String NAMESPACE = "menumapper.";
    
	@Override
	public List<UserRecipeResponse> selectRecipe(HashMap<String, Object> param) {
		return session.selectList("recipemapper.selectRecipe",param);
	}

	@Override
	public List<RcpMethod> selectRcpMethods() {
        return session.selectList(NAMESPACE + "selectRcpMethods");
	}

	@Override
	public void insertImage(Image mainImageVo) {
        session.insert(NAMESPACE + "insertImage", mainImageVo);
		
	}

	@Override
	public void insertNutrient(Nutrient totalNutrient) {
        session.insert(NAMESPACE + "insertNutrient", totalNutrient);
	}

	@Override
	public void insertRecipeStep(RecipeStep recipe) {
        session.insert(NAMESPACE + "insertRecipe", recipe);	
	}

	@Override
	public void insertRecipe(Recipe step) {
        session.insert(NAMESPACE + "insertRecipeStep", step);
	}

	@Override
	public List<RcpSituation> selectRcpSituations() {
		return session.selectList(NAMESPACE+"selectRcpSituations");
	}

	@Override
	public List<IngredientInfo> searchIngredients(String keyword) {
		return session.selectList(NAMESPACE + "searchIngredients", keyword);
	}
	

}

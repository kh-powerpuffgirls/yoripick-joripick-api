package com.kh.ypjp.community.recipe.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;


public class UserRecipeDaoImpl implements UserRecipeDao{


	@Autowired
	private SqlSessionTemplate session;
	
	@Override
	public List<UserRecipeResponse> selectRecipe(HashMap<String, Object> param) {
		return session.selectList("recipemapper.selectRecipe",param);
	}
	

}

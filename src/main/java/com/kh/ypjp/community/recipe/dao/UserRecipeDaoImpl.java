package com.kh.ypjp.community.recipe.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import org.springframework.stereotype.Repository;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRecipeDaoImpl implements UserRecipeDao{

	private SqlSessionTemplate session;
	
	@Override
	public List<UserRecipeResponse> selectRecipe(HashMap<String, Object> param) {
		return session.selectList("recipemapper.selectRecipe",param);
	}
	

}

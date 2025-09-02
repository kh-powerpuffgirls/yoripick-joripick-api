package com.kh.ypjp.community.recipe.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.ypjp.community.recipe.dao.UserRecipeDao;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;

@Service
public class UserRecipeSerivceImpl implements UserRecipeService {

	@Autowired
	private UserRecipeDao dao;
	
	@Override
	public List<UserRecipeResponse> selectRecipe(HashMap<String, Object> param) {
		return dao.selectRecipe(param);
	}

}

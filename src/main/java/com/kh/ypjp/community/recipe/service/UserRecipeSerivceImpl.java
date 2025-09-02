package com.kh.ypjp.community.recipe.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.ypjp.community.recipe.dao.UserRecipeDao;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRecipeSerivceImpl implements UserRecipeService {

	private UserRecipeDao dao;
	
	@Override
	public List<UserRecipeResponse> selectRecipe(HashMap<String, Object> param) {
		return dao.selectRecipe(param);
	}

}
